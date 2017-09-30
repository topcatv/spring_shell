package org.topcat.autodeploy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.topcat.autodeploy.utils.DeployEnvironment;
import org.topcat.autodeploy.utils.ShellUtils;

import java.io.IOException;
import java.util.Optional;

@Component
public class Init {

    public static final Logger logger = LoggerFactory.getLogger(Init.class);

    public void preEnv() {
        if (!DeployEnvironment.checked) {
            DeployEnvironment.check();
        }
        if (!DeployEnvironment.yumReady) {
            logger.info("替换源");
            replaceYumRepo();
        }
        if (!DeployEnvironment.pythonReady) {
            logger.info("安装python");
            yumInstall("python");
        }
        if (!DeployEnvironment.sshpassReady) {
            logger.info("安装sshpass");
            yumInstall("sshpass");
        }
        if (!DeployEnvironment.pipReady) {
            logger.info("pip");
            installPip();
        }
        logger.info("安装ansible");
        pipInstall("ansible");
    }

    private void installPip() {
        logger.info("开始安装pip.......");
        try {
            ShellUtils.runCommand("curl -o ~/get-pip.py https://bootstrap.pypa.io/get-pip.py && python ~/get-pip.py", Optional.empty());
            ShellUtils.runCommand("mkdir ~/.pip && echo -e \"[global]\\nindex-url = http://mirrors.163.com/pypi/simple/\\n[install]\\ntrusted-host=mirrors.163.com\" > ~/.pip/pip.conf", Optional.empty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replaceYumRepo() {
        try {
            ShellUtils.runCommand("mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup", Optional.empty());
            ShellUtils.runCommand("curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.163.com/.help/CentOS7-Base-163.repo", Optional.empty());
            ShellUtils.runCommand("yum clean all && yum makecache", Optional.empty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pipInstall(String pkg) {
        try {
            logger.info("开始pip安装" + pkg + ".......");
            ShellUtils.runCommand("pip install " + pkg, Optional.empty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void yumInstall(String pkg) {
        try {
            String result = ShellUtils.runCommand("rpm -qa | grep -w " + pkg, Optional.empty());
            if (StringUtils.isBlank(result)) {
                logger.info("开始yum安装" + pkg + ".......");
                ShellUtils.runCommand("yum install -y " + pkg, Optional.empty());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
