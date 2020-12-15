import com.github.janneri.innerbuildergeneratorintellijplugin.BuilderGenerator
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase


class SimpleCodeInsightTest : LightJavaCodeInsightFixtureTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun test_can_generate_a_builder_from_a_simple_dto() {
        myFixture.configureByFiles("SimpleTestDto.java")

        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            BuilderGenerator.generateBuilder(getSourceClass(), null, true)
        }

        myFixture.checkResultByFile("SimpleTestDtoResult.java")
    }

    fun test_can_regenerate_a_builder() {
        myFixture.configureByFiles("RegenerateDto.java")

        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            BuilderGenerator.generateBuilder(getSourceClass(), null, true)
        }

        myFixture.checkResultByFile("RegenerateDtoResult.java")
    }

    fun test_regenerate_removes_extra_fields_from_the_builder() {
        myFixture.configureByFiles("RegenerateRemoveField.java")

        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            BuilderGenerator.generateBuilder(getSourceClass(), null, true)
        }

        myFixture.checkResultByFile("RegenerateRemoveFieldResult.java")
    }

    private fun getSourceClass(): PsiClass {
        val psiJavaFile = myFixture.file as PsiJavaFile
        return psiJavaFile.classes.first()
    }
}