import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorOptions
import com.github.janneri.innerbuildergeneratorintellijplugin.SampleCodeGenerator
import junit.framework.TestCase

class SampleCodeGeneratorTest: TestCase() {
    fun test_sample_code_is_generated_for_the_options_dialog() {
        val codeSample = SampleCodeGenerator.generateSample(
            GeneratorOptions(true, "", "", true))

        val codelines = codeSample.lines()
        assertEquals("@JsonDeserialize(builder = SampleDto.Builder.class)", codelines.first())
        assertEquals("public class SampleDto {", codelines[1])
        assertEquals("    public final String foo;", codelines[2])
        assertEquals("    private SampleDto(Builder builder) {", codelines[4])
        assertEquals("}", codelines.last())
    }
}