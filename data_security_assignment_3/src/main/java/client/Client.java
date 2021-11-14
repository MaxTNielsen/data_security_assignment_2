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

        String user_1 = "Bob";
        String pass_1 = "pass_1";
        pass_1 = DigestUtils.sha256Hex(pass_1);
        printer.start(pass_1, user_1);
        printer.setConfig(pass_1, user_1, "Margin", "0,5");
        printer.readConfig(pass_1, user_1, "Margin");
        printer.status(pass_1, user_1,"printer2");
        printer.stop(pass_1, user_1);
        printer.start(pass_1, user_1);

        String user_2 = "Cecilia";
        String pass_2 = "pass_2";
        pass_2 = DigestUtils.sha256Hex(pass_2);
        printer.print(pass_2, user_2,"file1", "printer0");
        printer.print(pass_2, user_2,"file2", "printer1");
        printer.queue(pass_2, user_2,"printer0");
        printer.topQueue(pass_2, user_2,"printer0", 1);
        printer.queue(pass_2, user_2,"printer0");
        printer.print(pass_2, user_2,"file4", "printer1");
        printer.restart(pass_2, user_2);
        printer.print(pass_2, user_2,"file2", "printer2");
        printer.print(pass_2, user_2,"file4", "printer4");
        printer.print(pass_2, user_2,"file7", "printer2");
        printer.readConfig(pass_2, user_2, "Margin");
        
        String user_3 = "David";
        String pass_3 = "pass_3";
        pass_3 = DigestUtils.sha256Hex(pass_3);
        // String pass2_sha = DigestUtils.sha256Hex(pass2);
        printer.print(pass_3, user_3,"file2", "printer1");
        printer.queue(pass_3, user_2,"printer0");
        printer.stop(pass_3, user_3);
    }
}
