public class RegenerateDto {
    public final String foo;
    public final String bar;

    private RegenerateDto(Builder builder) {
        foo = builder.foo;
        bar = builder.bar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copy(RegenerateDto src) {
        Builder builder = new Builder();
        builder.foo = src.foo;
        builder.bar = src.bar;
        return builder;
    }

    public static class Builder {
        private String foo;
        private String bar;

        private Builder() {
        }

        public Builder foo(String foo) {
            this.foo = foo;
            return this;
        }

        public Builder bar(String bar) {
            this.bar = bar;
            return this;
        }

        public RegenerateDto build() {
            return new RegenerateDto(this);
        }
    }
}