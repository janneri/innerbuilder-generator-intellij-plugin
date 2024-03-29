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

    public static class Builder {
        private String foo;
        private Optional<String> bar = Optional.empty();

        private Builder() {
        }

        public Builder foo(String val) {
            foo = val;
            return this;
        }

        public Builder bar(Optional<String> val) {
            bar = val;
            return this;
        }

        public SimpleTestDto build() {
            return new SimpleTestDto(this);
        }
    }
}