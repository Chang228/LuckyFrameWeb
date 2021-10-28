package com.luckyframe.framework.ldap;

public interface ILdapPersonDao {

    public Person checkUserPassword(String uid, String password);
}