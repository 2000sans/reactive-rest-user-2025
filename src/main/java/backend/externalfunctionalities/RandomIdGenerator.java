package backend.externalfunctionalities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RandomIdGenerator {

    private LocalDateTime var1;
    private DateTimeFormatter var2;
    private StringBuilder var3;

    public RandomIdGenerator() {

        var1 = LocalDateTime.now();
        var2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        var3 = new StringBuilder(var1.format(var2));

    }

    public StringBuilder getGeneratedId(String param) {

        var3.replace(2,3,"");
        var3.replace(4,5,"");
        var3.replace(8,9,"");
        var3.replace(10,11,"");
        var3.replace(12,13,"");

        if(param.equals("admin"))
            var3.insert(0,"admin-");
        else if(param.equals("client"))
            var3.insert(0,"client-");
        else
            var3.insert(0,"default-");

        return var3;

    }
}
