package com.example.holdemcareer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.holdemcareer.data.local.LocalGamePersistence
import com.example.holdemcareer.domain.ai.AiDecisionEngine
import com.example.holdemcareer.domain.ai.OpponentCatalog
import com.example.holdemcareer.domain.career.CareerState
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameEngine
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.ui.screens.CareerHubScreen
import com.example.holdemcareer.ui.theme.HoldemCareerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var persistence: LocalGamePersistence
    private val gameEngine = GameEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        persistence = LocalGamePersistence(applicationContext)
        val initialCareer = persistence.loadCareerState()

        val humanId = "human_player"
        val tableOpponents = OpponentCatalog.allOpponents.take(5)

        val initialPlayers = listOf(
            PlayerState(id = humanId, name = "You", chips = initialCareer.currentRunBankroll, isHuman = true, seatIndex = 0)
        ) + tableOpponents.mapIndexed { idx, opp ->
            PlayerState(id = opp.id, name = opp.name, chips = 50, isHuman = false, seatIndex = idx + 1)
        }

        gameEngine.startNewHand(initialPlayers, dealerIndex = 0, smallBlind = 1, bigBlind = 2)

        setContent {
            HoldemCareerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF111111)
                ) {
                    HoldemCareerApp(
                        gameEngine = gameEngine,
                        humanPlayerId = humanId,
                        initialCareer = initialCareer,
                        onSaveCareer = { persistence.saveCareerState(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun HoldemCareerApp(
    gameEngine: GameEngine,
    humanPlayerId: String,
    initialCareer: CareerState,
    onSaveCareer: (CareerState) -> Unit
) {
    var gameState by remember { mutableStateOf(gameEngine.state) }
    var careerState by remember { mutableStateOf(initialCareer) }

    // Auto-advance AI turns
    LaunchedEffect(gameState) {
        if (gameState.currentRound != BettingRound.ENDED) {
            val activePlayer = gameState.players.getOrNull(gameState.activePlayerIndex)
            if (activePlayer != null && !activePlayer.isHuman && !activePlayer.isFolded && !activePlayer.isAllIn) {
                delay(600) // Realistic delay for AI decision
                val oppProfile = OpponentCatalog.getById(activePlayer.id)
                val aiAction = AiDecisionEngine.decideAction(oppProfile, gameState, activePlayer)
                gameEngine.applyAction(aiAction)
                gameState = gameEngine.state
            }
        }
    }

    CareerHubScreen(
        careerState = careerState,
        gameState = gameState,
        humanPlayerId = humanPlayerId,
        onActionSelected = { action ->
            gameEngine.applyAction(action)
            gameState = gameEngine.state
        },
        onNextHandClicked = {
            // Update human bankroll from ended hand
            val humanPlayer = gameState.players.firstOrNull { it.id == humanPlayerId }
            if (humanPlayer != null) {
                val updatedCareer = careerState.copy(
                    currentRunBankroll = humanPlayer.chips,
                    totalHandsPlayedAllTime = careerState.totalHandsPlayedAllTime + 1,
                    totalHandsPlayedCurrentRun = careerState.totalHandsPlayedCurrentRun + 1
                )
                careerState = updatedCareer
                onSaveCareer(updatedCareer)
            }

            // Start next hand
            val nextDealer = (gameState.dealerIndex + 1) % gameState.players.size
            val resetPlayers = gameState.players.map {
                if (it.chips <= 0) it.copy(chips = 50) else it
            }
            gameEngine.startNewHand(resetPlayers, dealerIndex = nextDealer, smallBlind = 1, bigBlind = 2)
            gameState = gameEngine.state
        }
    )
}
