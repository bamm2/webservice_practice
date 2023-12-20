package com.yubeom.study.springboot.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yubeom.study.springboot.web.controller.HelloController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class) //  @Autowired, @MockBean 등만 AppCtx 로 로딩하는 역할을 수행.
@WebMvcTest(controllers = HelloController.class)   // JUnit5에서는 RunWith 내장 , 컨트롤러 동작만 확인할 경우 사용하는 어노테이션
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc; // 목 객체 , 웹 API 테스트용

    @Test
    public void hello_리턴() throws Exception {
        // given
        String hello = "hello";

        // when && then
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));

    }

    @Test
    public void helloResponseDto_리턴() throws Exception {
        //given
        String name = "이름";
        int amount = 1;

        //when && then
        mvc.perform(get("/hello/dto")
                        .param("name", name)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)));
    }

}