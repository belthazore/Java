package hello;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();
    //  private List<Contact> contactsList = new ArrayList<Contact>();


    /*
    REST поиск клиентов в БД по маске RegExp

    ТЗ
    1. /hello/contacts?nameFilter=^A.*$​ - возвращает контакты, которые НЕ начинаются с A
    http://localhost:8080/contacts?nameFilter=%5EA.*%24

    2. /hello/contacts?nameFilter=^.*[aei].*$​ - возвращает контакты, которые НЕ содержат букв a, e, i
    http://localhost:8080/contacts?nameFilter=%5E.*%5Baei%5D.*%24

    */
    @RequestMapping(value = "/contacts", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Obj rest(@RequestParam(value = "nameFilter", required = false) String regExpr) {

        List<Contact> contactsList = Database.searchByRegExp(regExpr);

        // В зависимости от рез-та поиска(колич-ва контактов) сформируем комментарий
        int contacts = contactsList.size();
        String result;
        if (contacts == 0) { //todo брать длинну от возвращенного List от Database
            throw new ContactsNotFoundException();
        } else if (contacts == 10) {
            result = "Founded more than 10 contacts";
        } else {
            result = "ok";
        }

        return new Obj(result, contactsList);
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