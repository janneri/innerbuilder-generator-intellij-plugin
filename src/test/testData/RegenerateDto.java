public class RegenerateDto {
    public final String foo;
    public final String bar;

    private RegenerateDto(Builder builder) {
        this.foo = builder.foo;
    }

    public static Builder builder() {
        return new Builder();
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