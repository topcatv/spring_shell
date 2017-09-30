package org.topcat.autodeploy.web;

import org.apache.commons.io.IOUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.topcat.autodeploy.Init;
import org.topcat.autodeploy.utils.DeployEnvironment;
import org.topcat.autodeploy.utils.ShellUtils;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping({"/", "/index"})
public class IndexController {

    @Resource
    private Init init;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream("/var/log/autodeploy.log"), "UTF-8");
        model.addAttribute("lines", lines);
        return "index";
    }

    @RequestMapping("/exec")
    public String execPage() {
        return "exec";
    }

    @RequestMapping("/ws")
    public String ws() {
        return "ws";
    }

    @RequestMapping("/ws2")
    public String ws2() {
        return "ws2";
    }

    @RequestMapping("/do")
    @ResponseBody
    public List<String> done() throws IOException {
        String s = ShellUtils.runCommand("echo hello", Optional.empty());
        FileOutputStream output = new FileOutputStream("/var/log/1.log");
        IOUtils.write(s, output, "UTF-8");
        IOUtils.closeQuietly(output);

        return IOUtils.readLines(new FileInputStream("/var/log/1.log"), "UTF-8");
    }

    @RequestMapping("/checkEnv")
    @ResponseBody
    public Map<String, Boolean> checkEnvironment() {
        DeployEnvironment.check();
        Map<String, Boolean> map = new HashMap<>();
        map.put("yum", DeployEnvironment.yumReady);
        map.put("python", DeployEnvironment.pythonReady);
        map.put("sshpass", DeployEnvironment.sshpassReady);
        map.put("pip", DeployEnvironment.pipReady);
        map.put("ansible", DeployEnvironment.ansibleReady);
        return map;
    }

    @RequestMapping("/prepareEnv")
    @ResponseBody
    public Map<String, Boolean> prepareEnv() {
        init.preEnv();
        DeployEnvironment.check();
        Map<String, Boolean> map = new HashMap<>();
        map.put("yum", DeployEnvironment.yumReady);
        map.put("python", DeployEnvironment.pythonReady);
        map.put("sshpass", DeployEnvironment.sshpassReady);
        map.put("pip", DeployEnvironment.pipReady);
        map.put("ansible", DeployEnvironment.ansibleReady);
        return map;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("ansible all -i ~/docker-ssh/hosts.yml -m ping -u admin -k");
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        process.waitFor();
        while ((line = br.readLine()) != null) {
            System.out.println(">>>>>>>>>"+line);
        }
        //读取标准错误流
        BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errline = null;
        while ((errline = brError.readLine()) != null) {
            System.out.println("??????????"+errline);
        }
        return line;
    }

    @MessageMapping("/send")
    @SendTo("/topic/send")
    public String send(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
