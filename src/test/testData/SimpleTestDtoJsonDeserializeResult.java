@JsonDeserialize(builder = SimpleTestDto.Builder.class)
public class SimpleTestDto {
    public final String foo;
    public final Optional<String> bar;

    private SimpleTestDto(Builder builder) {
        foo = builder.foo;
        bar = builder.bar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copy(SimpleTestDto src) {
        Builder builder = new Builder();
        builder.foo = src.foo;
        builder.bar = src.bar;
        return builder;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String foo;
        private Optional<String> bar = Optional.empty();

        private Builder() {
        }

        public Builder foo(String foo) {
            this.foo = foo;
            return this;
        }

        public Builder bar(Optional<String> bar) {
            this.bar = bar;
            return this;
        }

        public SimpleTestDto build() {
            return new SimpleTestDto(this);
        }
    }
}