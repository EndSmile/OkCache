package com.xdja.cache.retrofit.bean;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample.retrofit</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/8/3</p>
 * <p>Time:9:01</p>
 */


public class Contributor {
    private String login;
    private int contributions;

    public Contributor(String login, int contributions) {
        this.login = login;
        this.contributions = contributions;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}
