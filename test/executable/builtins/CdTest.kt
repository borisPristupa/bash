package executable.builtins

import com.boris.bash.DictEnvironment
import com.boris.bash.Resources
import com.boris.bash.environment.SystemDelegatingDictEnvironment
import com.boris.bash.environment.VarName
import com.boris.bash.environment.homeDirectory
import com.boris.bash.executable.builtins.Cd
import com.boris.bash.executable.builtins.getCommand
import com.boris.bash.executable.builtins.test
import java.nio.file.Path
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CdTest {
    @Test
    fun wrongNumberOfArguments() {
        Cd.Builder.getCommand("1", "2").test(code = 1)
        Cd.Builder.getCommand("1", "2", "3").test(code = 1)
    }

    @Test
    fun noSuchFile() {
        Cd.Builder.getCommand(UUID.randomUUID().toString()).test(code = 2)
    }

    @Test
    fun cdToFile() {
        Cd.Builder.getCommand(pathOf("file.txt").toString()).test(code = 3)
    }

    @Test
    fun noArgs() {
        val env = mutableMapOf<String, String>()
        Cd.Builder.getCommand().test(environment = DictEnvironment(env))
        assertEquals(SystemDelegatingDictEnvironment().homeDirectory.toString(), env[VarName.WORKING_DIR.repr])
    }

    @Test
    fun toDir() {
        val map = mutableMapOf<String, String>()
        val env = DictEnvironment(map)
        Cd.Builder.getCommand(pathOf("inner").toString()).test(environment = env)
        assertEquals(pathOf("inner").toAbsolutePath().toString(), map[VarName.WORKING_DIR.repr])
        Cd.Builder.getCommand(Path.of(".", "..").toString()).test(environment = env)
        assertEquals(pathOf().toAbsolutePath().toString(), map[VarName.WORKING_DIR.repr])
    }

    private fun pathOf(vararg components: String): Path = Resources.pathOf("test_cd", *components)
}
