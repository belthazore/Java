package hello;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GreetingController {

    /*

    ТЗ
    1. /hello/contacts?nameFilter=^A.*$​ - возвращает контакты, которые НЕ начинаются с A
    http://localhost:8080/contacts?nameFilter=%5EA.*%24                    (для spring-boot:run)
    http://localhost:8080/hello/contacts?nameFilter=%5EA.*%24              (для tomcat8:redeploy)

    2. /hello/contacts?nameFilter=^.*[aei].*$​ - возвращает контакты, которые НЕ содержат букв a, e, i
    http://localhost:8080/contacts?nameFilter=%5E.*%5Baei%5D.*%24          (для spring-boot:run)
    http://localhost:8080/hello/contacts?nameFilter=%5E.*%5Baei%5D.*%24    (для tomcat8:redeploy)

    Dbg поиск по ID
    http://localhost:8080/search_by_id?id=1                                (для spring-boot:run)
    http://localhost:8080/hello/search_by_id?id=1                          (для tomcat8:redeploy)

    fillDb
    curl http://localhost:8080/fillDb?count=1                              (для spring-boot:run)
    curl http://localhost:8080/hello/fillDb?count=1                        (для tomcat8:redeploy)
    */
    @RequestMapping(value = "/contacts", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Obj contacts(@RequestParam(value = "nameFilter", required = false) String regExpr) {

        List<Contact> contactsList = Util.findByRegExp(regExpr);

        // В зависимости от рез-та поиска(колич-ва контактов) сформируем комментарий
        int contacts = contactsList.size();
        String result;
        if (contacts == 0) { //todo брать длинну от возвращенного List от Util
            throw new ContactsNotFoundException();
        } else if (contacts == 10) {
            result = "Founded more than 10 contacts";
        } else {
            result = "ok";
        }

        return new Obj(result, contactsList);
    }

    @RequestMapping(value = "/search_by_id", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Obj search_by_id(@RequestParam(value = "id", required = true) int id) {
        System.out.println();
        return new Obj("ok", Util.findById(id));
    }


    // Заполнить БД записями (при RAM 1Gb в вирт. машине лучше генерить по ~250 000 записей)
    @RequestMapping(value = "/fillDb", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Body fillDb(@RequestParam(value = "count") int count) {
        List<Contact> contactsList = new ArrayList<>();
        FillDb.start(count);
        return new Body(contactsList, contactsList.size());
    }










    // Присвоить статус ответу.
    // 1. Через response
    @RequestMapping(value = "/status_by_resp", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Body status_by_resp(HttpServletResponse response) {
        response.setStatus(504);
        return new Body(new ArrayList<>(), 0);
    }

    // 2. Через exception
    @RequestMapping(value = "/status_by_exception", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Body status_by_exception() {
        // тут throw new ..
        throw new ContactsNotFoundException();
//        return new Body(new ArrayList<>(), 0);
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No contacts found")  // 404
    private class ContactsNotFoundException extends RuntimeException {
        // ...
    }

    /*
    @ResponseStatus(value=HttpStatus. NOT_FOUND, reason="No such Contacts")  // 404
    private class ContactsNotFoundException extends RuntimeException {
        // ...
    }
    */
}