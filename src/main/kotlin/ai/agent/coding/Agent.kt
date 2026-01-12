package ai.agent.coding

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.file.EditFileTool
import ai.koog.agents.ext.tool.file.ListDirectoryTool
import ai.koog.agents.ext.tool.file.ReadFileTool
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.agents.features.opentelemetry.integration.weave.addWeaveExporter
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.rag.base.files.JVMFileSystemProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

val logger = KotlinLogging.logger {}

fun main() {
    val apiKey = System.getenv("ANTHROPIC_API_KEY") ?: error("ANTHROPIC_API_KEY is required.")

    val agent = AIAgent(
        id = "coding-agent",
        promptExecutor = simpleAnthropicExecutor(apiKey),
        toolRegistry = ToolRegistry {
            tool(ListDirectoryTool(JVMFileSystemProvider.ReadOnly))
            tool(ReadFileTool(JVMFileSystemProvider.ReadOnly))
            tool(EditFileTool(JVMFileSystemProvider.ReadWrite))
        },
        agentConfig = AIAgentConfig(
            prompt = prompt("coding-prompt") {
                system("You are a helpful coding assistant")
            },
            model = AnthropicModels.Sonnet_4_5,
            maxAgentIterations = 1000,
        ),
        strategy = singleRunStrategy(),
    ) {
    }

    println("Enter agent task:")
    val agentTask = readln()
    val agentResult = runBlocking {
        agent.run(agentTask)
    }

    logger.info { "AI agent was successfully terminated with result $agentResult" }
}