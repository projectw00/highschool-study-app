package com.example.highschoolstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.highschoolstudy.ui.theme.HighSchoolStudyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HighSchoolStudyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyApp()
                }
            }
        }
    }
}

@Composable
fun StudyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "subjects") {
        composable("subjects") {
            SubjectListScreen(onSubjectClick = { subject ->
                navController.navigate("units/${subject}")
            })
        }
        composable("units/{subject}") { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject") ?: ""
            UnitListScreen(subject = subject, onUnitClick = { unit ->
                navController.navigate("study/$subject/$unit")
            })
        }
        composable("study/{subject}/{unit}") { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: ""
            StudyScreen(subject = subject, unit = unit)
        }
    }
}

@Composable
fun SubjectListScreen(onSubjectClick: (String) -> Unit) {
    val subjects = listOf("확률과 통계") // 확장 가능
    Column(modifier = Modifier.padding(16.dp)) {
        Text("과목 선택 (고2 기준)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        subjects.forEach { subject ->
            Button(
                onClick = { onSubjectClick(subject) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(subject)
            }
        }
    }
}

@Composable
fun UnitListScreen(subject: String, onUnitClick: (String) -> Unit) {
    val units = when (subject) {
        "확률과 통계" -> listOf("경우의 수", "확률", "통계")
        else -> emptyList()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("$subject 단원 선택", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        units.forEach { unit ->
            Button(
                onClick = { onUnitClick(unit) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(unit)
            }
        }
    }
}

@Composable
fun StudyScreen(subject: String, unit: String) {
    val content = getContent(subject, unit)
    var currentProblemIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("$unit 개념", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(content.concept)
            Spacer(modifier = Modifier.height(24.dp))
            Text("쉬운 문제 풀기", style = MaterialTheme.typography.headlineSmall)
        }

        if (content.problems.isNotEmpty()) {
            val problem = content.problems[currentProblemIndex]
            item {
                Text(problem.question, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                problem.options.forEachIndexed { index, option ->
                    Button(
                        onClick = { showAnswer = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("${index + 1}. $option")
                    }
                }
                if (showAnswer) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("정답: ${problem.options[problem.answer]}")
                    Text("해설: ${problem.explanation}")
                    if (currentProblemIndex < content.problems.size - 1) {
                        Button(onClick = {
                            currentProblemIndex++
                            showAnswer = false
                        }) {
                            Text("다음 문제")
                        }
                    }
                }
            }
        }
    }
}

data class Problem(
    val question: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String
)

data class UnitContent(
    val concept: String,
    val problems: List<Problem>
)

fun getContent(subject: String, unit: String): UnitContent {
    return when (unit) {
        "경우의 수" -> UnitContent(
            concept = "중복순열: n개 중에서 중복을 허락하여 r개를 선택하는 경우의 수 = n^r\n같은 것이 있는 순열: n개 중 같은 것이 k개 있을 때 순열 = n! / k!\n중복조합: n개 중에서 중복을 허락하여 r개를 선택하는 경우의 수 = C(n+r-1, r)\n이항정리: (a+b)^n 전개 공식",
            problems = listOf(
                Problem(
                    question = "3개의 문자 A, B, C에서 중복을 허락하여 2개를 선택하는 경우의 수는?",
                    options = listOf("6", "9", "3", "12"),
                    answer = 1,
                    explanation = "중복순열 3^2 = 9"
                ),
                Problem(
                    question = "알파벳 A, B, C가 각각 2개씩 있을 때 순열의 수는?",
                    options = listOf("6", "720", "90", "120"),
                    answer = 2,
                    explanation = "6! / (2!2!2!) = 90"
                )
            )
        )
        "확률" -> UnitContent(
            concept = "확률의 기본 성질: 0 ≤ P(A) ≤ 1, P(S)=1\n덧셈정리: P(A∪B) = P(A)+P(B)-P(A∩B)\n여사건: P(A^c) = 1 - P(A)\n조건부확률: P(A|B) = P(A∩B)/P(B)\n독립사건: P(A∩B)=P(A)P(B)",
            problems = listOf(
                Problem(
                    question = "주사위를 던질 때 3이 나올 확률은?",
                    options = listOf("1/2", "1/3", "1/6", "1/4"),
                    answer = 2,
                    explanation = "가능한 경우 6개 중 1개이므로 1/6"
                ),
                Problem(
                    question = "P(A)=0.4, P(B)=0.3, P(A∩B)=0.1일 때 P(A∪B)는?",
                    options = listOf("0.6", "0.7", "0.5", "0.8"),
                    answer = 0,
                    explanation = "0.4 + 0.3 - 0.1 = 0.6"
                )
            )
        )
        "통계" -> UnitContent(
            concept = "확률변수: 무작위의 결과를 수로 나타내는 함수\n이산확률변수의 기댓값(E(X)) = Σ x_i * P(X=x_i)\n분산 = E(X^2) - [E(X)]^2\n이항분포: n번 독립시험에서 성공 횟수의 분포 B(n,p)\n정규분포: 연속확률분포의 대표\n모평균 추정, 모비율 추정 (검정된 내용)",
            problems = listOf(
                Problem(
                    question = "확률변수 X가 1,2,3의 값을 각각 1/3 확률로 가질 때 기댓값은?",
                    options = listOf("1", "2", "3", "1.5"),
                    answer = 1,
                    explanation = "(1+2+3)/3 = 2"
                ),
                Problem(
                    question = "이항분포 B(10, 0.5)에서 기댓값은?",
                    options = listOf("5", "2.5", "10", "0.5"),
                    answer = 0,
                    explanation = "np = 10*0.5 = 5"
                )
            )
        )
        else -> UnitContent("내용 준비 중", emptyList())
    }
}
