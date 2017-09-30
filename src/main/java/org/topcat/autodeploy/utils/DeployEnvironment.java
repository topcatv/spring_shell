package org.topcat.autodeploy.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class DeployEnvironment {
    public static boolean yumReady = false;
    public static boolean pythonReady = false;
    public static boolean pipReady = false;
    public static boolean ansibleReady = false;
    public static boolean sshpassReady = false;
    public static boolean checked = false;
    public static void check() {
        try {
            yumReady = checkYumSource();
            pythonReady = checkPython();
            sshpassReady = checkSshpass();
            pipReady = checkPip();
            ansibleReady = checkAnsible();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checked = true;
    }

    private static boolean checkSshpass() throws IOException {
        return StringUtils.isNotBlank(ShellUtils.runCommand("rpm -qa | grep -w sshpass", Optional.empty()));
    }

    private static boolean checkAnsible() throws IOException {
        return StringUtils.startsWith(ShellUtils.runCommand("ansible --version", Optional.empty()), "ansible");
    }

    private static boolean checkPip() throws IOException {
        return StringUtils.startsWith(ShellUtils.runCommand("pip -V", Optional.empty()), "pip");
    }

    private static boolean checkPython() throws IOException {
        return StringUtils.isNotBlank(ShellUtils.runCommand("rpm -qa | grep -w python", Optional.empty()));
    }

    private static boolean checkYumSource() throws IOException {
        return StringUtils.isNotBlank(ShellUtils.runCommand("cat /etc/yum.repos.d/CentOS-Base.repo | grep 163", Optional.empty()));
    }
}
