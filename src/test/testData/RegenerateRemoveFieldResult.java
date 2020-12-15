public class RegenerateDto {
    public final String foo;

    private RegenerateDto(Builder builder) {
        foo = builder.foo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copy(RegenerateDto src) {
        Builder builder = new Builder();
        builder.foo = src.foo;
        return builder;
    }

    public static class Builder {
        private String foo;

        private Builder() {
        }

        public Builder foo(String foo) {
            this.foo = foo;
            return this;
        }

        public RegenerateDto build() {
            return new RegenerateDto(this);
        }
    }
}