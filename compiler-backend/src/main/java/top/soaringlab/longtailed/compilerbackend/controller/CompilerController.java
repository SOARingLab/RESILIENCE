package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.compilerbackend.dsl.grocerystore.GroceryStoreTranslator;
import top.soaringlab.longtailed.compilerbackend.dsl.simple.SimpleTranslator;
import top.soaringlab.longtailed.compilerbackend.service.CompilerService;

@RestController
@RequestMapping(value = "/api/compiler")
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @PostMapping(value = "/constraint-remove-all")
    public String constraintRemoveAll(@RequestBody String file) throws Exception {
        return compilerService.constraintRemoveAll(file);
    }

    @PostMapping(value = "/compile")
    public String compile(@RequestBody String file) throws Exception {
        return compilerService.compile("", file);
    }

    @PostMapping(value = "/compile-script")
    public String compileScript(@RequestBody String text) throws Exception {
        return compilerService.compileScript(text);
    }

    @PostMapping(value = "/grocery-store-translate")
    public String groceryStoreTranslate(@RequestBody String text) throws Exception {
        return GroceryStoreTranslator.translate(text);
    }

    @PostMapping(value = "/simple-translate")
    public String simpleTranslate(@RequestBody String text) throws Exception {
        return SimpleTranslator.translate(text);
    }
}
