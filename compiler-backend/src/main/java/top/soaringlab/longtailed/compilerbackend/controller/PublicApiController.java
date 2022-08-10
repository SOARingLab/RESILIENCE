package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;
import top.soaringlab.longtailed.compilerbackend.service.PublicApiService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/public-api")
public class PublicApiController {

    @Autowired
    private PublicApiService publicApiService;

    @GetMapping(value = "/find-all")
    public Page<PublicApi> findAll(Integer page, Integer size) {
        return publicApiService.findAll(page, size);
    }

    @GetMapping(value = "/find-by-process-id/{processId}")
    public List<PublicApi> findByProcessId(@PathVariable String processId) {
        return publicApiService.findByProcessId(processId);
    }

    @GetMapping(value = "/get-one/{publicApi}")
    public PublicApi getOne(@PathVariable PublicApi publicApi) {
        return publicApi;
    }

    @PostMapping(value = "/save")
    public PublicApi save(@RequestBody PublicApi publicApi) {
        return publicApiService.save(publicApi);
    }

    @DeleteMapping(value = "/delete/{publicApi}")
    public void delete(@PathVariable PublicApi publicApi) {
        publicApiService.delete(publicApi);
    }
}
