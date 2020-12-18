import com.github.janneri.innerbuildergeneratorintellijplugin.BuilderGenerator
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorOptions
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase

class GeneratorTest : LightJavaCodeInsightFixtureTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun test_can_generate_a_builder_from_a_simple_dto() {
        myFixture.configureByFiles("SimpleTestDto.java")

        runGenerator()

        myFixture.checkResultByFile("SimpleTestDtoResult.java")
    }

    fun test_can_regenerate_a_builder() {
        myFixture.configureByFiles("RegenerateDto.java")

        runGenerator()

        myFixture.checkResultByFile("RegenerateDtoResult.java")
    }

    fun test_regenerate_removes_extra_fields_from_the_builder() {
        myFixture.configureByFiles("RegenerateRemoveField.java")

        runGenerator()

        myFixture.checkResultByFile("RegenerateRemoveFieldResult.java")
    }

    fun test_generate_twice_should_lead_to_same_result_as_generate_once() {
        myFixture.configureByFiles("SimpleTestDto.java")

        runGenerator()
        runGenerator()

        myFixture.checkResultByFile("SimpleTestDtoResult.java")
    }

    private fun runGenerator() {
        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            val generator = BuilderGenerator(getSourceClass(), GeneratorOptions(true, ""))
            generator.generateBuilder()
        }
    }

    private fun getSourceClass(): PsiClass {
        val psiJavaFile = myFixture.file as PsiJavaFile
        return psiJavaFile.classes.first()
    }
}
