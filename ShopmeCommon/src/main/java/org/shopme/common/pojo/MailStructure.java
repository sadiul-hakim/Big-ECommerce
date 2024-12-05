package org.shopme.common.pojo;

public record MailStructure(String subject, String mail) {
    public MailStructure() {
        this("", "");
    }
}
