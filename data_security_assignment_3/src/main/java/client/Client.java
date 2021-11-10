package client;

import com.google.gson.Gson;
import com.printer.Cookie;
import com.printer.IPrinterServant;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import org.apache.commons.codec.digest.DigestUtils;

public class Client {
    private static Gson gson = new Gson();

    public static void main(String args[]) throws NotBoundException, IOException, InterruptedException {
        IPrinterServant printer = (IPrinterServant) Naming.lookup("rmi://localhost:5099/printer");

        String pass1 = "hello1";
        String pass1_sha = DigestUtils.sha256Hex(pass1);
        printer.print(pass1, "user1","file1", "printer0");
        printer.print(pass1, "user1","file2", "printer1");
        printer.queue(pass1, "user1","printer0");
        printer.topQueue(pass1, "user1","printer0", 1);
        printer.queue(pass1, "user1","printer0");
        printer.print(pass1, "user1","file4", "printer1");
        printer.restart(pass1, "user1");
        printer.print(pass1, "user1","file2", "printer2");
        printer.print(pass1, "user1","file4", "printer4");
        printer.print(pass1, "user1","file7", "printer2");
        printer.setConfig(pass1, "user1", "Margin", "0,5");
        printer.readConfig(pass1, "user1", "Margin");
        printer.status(pass1, "user1","printer2");
        printer.stop(pass1, "user1");

        String pass2 = "hello2";
        String pass2_sha = DigestUtils.sha256Hex(pass2);
        printer.print(pass2, "user1","file2", "printer1");
        printer.stop(pass2, "user1");
    }
}
