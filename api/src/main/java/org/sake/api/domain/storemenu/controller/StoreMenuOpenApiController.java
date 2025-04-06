package org.sake.api.domain.storemenu.controller;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.api.Api;
import org.sake.api.domain.storemenu.business.StoreMenuBusiness;
import org.sake.api.domain.storemenu.controller.model.StoreMenuRegisterRequest;
import org.sake.api.domain.storemenu.controller.model.StoreMenuResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")  // 모든 도메인에 대해 CORS 허용
@RequestMapping("/open-api/store-menu")
public class StoreMenuOpenApiController {

    private final StoreMenuBusiness storeMenuBusiness;

    @PostMapping("/register")
    public Api<StoreMenuResponse> register(
            @Valid
            @RequestBody Api<StoreMenuRegisterRequest> request
    ){
        var req = request.getBody();
        var response = storeMenuBusiness.register(req);
        return Api.OK(response);
    }
}
