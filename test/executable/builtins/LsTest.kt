package executable.builtins

import com.boris.bash.Resources
import com.boris.bash.executable.builtins.Ls
import com.boris.bash.executable.builtins.getCommand
import com.boris.bash.executable.builtins.test
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.UUID

internal class LsTest {
    @Test
    fun noArgs() {
        Ls.Builder.getCommand().test()
    }

    @Test
    fun pathNotExist() {
        val notFile = UUID.randomUUID().toString()
        Ls.Builder.getCommand(notFile).test(code = 2)
    }

    @Test
    fun wrongNumberOfArguments() {
        Ls.Builder.getCommand("1", "2").test(code = 1)
        Ls.Builder.getCommand("1", "2", "3").test(code = 1)
    }

    @Test
    fun absolutePath() {
        val path = pathOf().toAbsolutePath()
        Ls.Builder.getCommand(path.toString()).test(output = testContent)
    }

    @Test
    fun relativePath() {
        val path = pathOf("inner", ".", "..", "inner", ".", "..")
        Ls.Builder.getCommand(path.toString()).test(output = testContent)
    }

    private fun pathOf(vararg components: String): Path = Resources.pathOf("test_ls", *components)

    companion object {
        val testContent = """
                |a.txt
                |b.txt
                |inner
                |
            """.trimMargin()
    }
}
