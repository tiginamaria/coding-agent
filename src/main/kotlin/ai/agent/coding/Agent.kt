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
    val weaveEntity = System.getenv("WEAVE_ENTITY") ?: error("WEAVE_ENTITY is required.")
    val weaveProjectName = System.getenv("WEAVE_PROJECT_NAME") ?: error("WEAVE_PROJECT_NAME is required.")
    val weaveApiKey = System.getenv("WEAVE_API_KEY") ?: error("WEAVE_API_KEY is required.")

    val agent = AIAgent(
        id = "coding-agent",
        promptExecutor = simpleAnthropicExecutor(apiKey),
        toolRegistry = ToolRegistry {
            tool(ListDirectoryTool(JVMFileSystemProvider.ReadOnly))
            tool(ReadFileTool(JVMFileSystemProvider.ReadOnly))
            tool(EditFileTool(JVMFileSystemProvider.ReadWrite))
        },
        agentConfig = AIAgentConfig(
            // Initial prompt to user for agent
            prompt = prompt("coding-prompt") {
                system("You are a helpful coding assistant")
            },
            // Model to use for the agent, should match one of the models supported by the llm provider in prompt executor
            model = AnthropicModels.Sonnet_4_5,
            // Max number of agent steps to execute before force stop
            maxAgentIterations = 1000,
        ),
        // The logic of the agent
        strategy = singleRunStrategy(),
    ) {
    }

    print("Enter agent task: ")
    val agentTask = readln()
    val agentResult = runBlocking {
        agent.run(agentTask)
    }

    logger.info { "AI agent was successfully terminated with result $agentResult" }
    logger.info { "See traces on https://wandb.ai/$weaveEntity/$weaveProjectName/weave/traces" }
}