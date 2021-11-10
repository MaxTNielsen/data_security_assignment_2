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
        Cookie cookie = gson.fromJson(printer.start(pass1_sha, "user1"), Cookie.class);
        printer.print("file1", "printer0", gson.toJson(cookie));
        printer.print("file2", "printer1", gson.toJson(cookie));
        printer.queue("printer0",gson.toJson(cookie));
        printer.print("file3", "printer0", gson.toJson(cookie));
        printer.topQueue("printer0", 1, gson.toJson(cookie));
        printer.queue("printer0",gson.toJson(cookie));
        printer.print("file4", "printer1", gson.toJson(cookie));
        printer.restart(gson.toJson(cookie));
        printer.print("file2", "printer2", gson.toJson(cookie));
        printer.print("file4", "printer4", gson.toJson(cookie));
        printer.print("file7", "printer2", gson.toJson(cookie));
        printer.setConfig("Margin", "0,5", gson.toJson(cookie));
        printer.readConfig("Margin",gson.toJson(cookie));
        printer.status("printer2", gson.toJson(cookie));
        printer.stop(gson.toJson(cookie));

        String pass2 = "hello2";
        String pass2_sha = DigestUtils.sha256Hex(pass2);
        Cookie cookie2 = gson.fromJson(printer.start(pass2_sha, "user2"), Cookie.class);
        printer.print("file2", "printer1", gson.toJson(cookie2));
        printer.stop(gson.toJson(cookie2));

    }
}
