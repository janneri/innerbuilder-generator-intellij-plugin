package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.convertFirstLetterToUpperCase

object SampleCodeGenerator {
    fun generateSample(options: GeneratorOptions): String {
        val paramName = options.paramName.ifEmpty { "foo" }
        val methodName = if (options.methodPrefix.isNotEmpty()) {
            "${options.methodPrefix}${convertFirstLetterToUpperCase("foo")}"
        } else {
            "foo"
        }

        return """
            ${if (options.jsonDeserializeWithBuilder) "@JsonDeserialize(builder = SampleDto.Builder.class)" else "REMOVE"}
            public class SampleDto {
                public final String foo;

                private SampleDto(Builder builder) {
                    foo = builder.foo;
                }

                public static Builder builder() {
                    return new Builder();
                }
                
                ${if (options.generateCopyMethod) "public static Builder copy(SampleDto src) {" else "REMOVE"}
                ${if (options.generateCopyMethod) "    Builder builder = new Builder();" else "REMOVE"}
                ${if (options.generateCopyMethod) "    builder.foo = src.foo;" else "REMOVE"}
                ${if (options.generateCopyMethod) "    return builder;" else "REMOVE"}
                ${if (options.generateCopyMethod) "}" else "REMOVE"}
                
                ${if (options.jsonDeserializeWithBuilder) "@JsonPOJOBuilder(withPrefix = \"\")" else "REMOVE"}
                public static class Builder {
                    private String foo;
            
                    private Builder() { }
            
                    public Builder $methodName(String $paramName) {
                        this.foo = $paramName;
                        return this;
                    }
            
                    public SimpleTestDto build() {
                       return new SampleDto(this);
                    }
                }
            }
        """.trimIndent()
            .lines()
            .filter { !it.contains("REMOVE") }
            .joinToString("\n")
    }
}
