package com.luckyframe.project.system.dict.domain;

public class DictItem {
    private String value;
    private String text;

    DictItem(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public static DictItemBuilder builder() {
        return new DictItemBuilder();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static class DictItemBuilder {
        private String value;
        private String text;

        DictItemBuilder() {
        }

        public DictItemBuilder value(String value) {
            this.value = value;
            return this;
        }

        public DictItemBuilder text(String text) {
            this.text = text;
            return this;
        }

        public DictItem build() {
            return new DictItem(value, text);
        }

        public String toString() {
            return "DictItem.DictItemBuilder(value=" + this.value + ", text=" + this.text + ")";
        }
    }
}
