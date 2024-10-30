package hello.hello_spring.controller;

import hello.hello_spring.service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gpt")
public class GPTController {

    private final GPTService gptService;

    @Autowired
    public GPTController(GPTService gptService) {
        this.gptService = gptService;
    }

    @GetMapping("/get-response")
    public String getResponse(@RequestParam("prompt") String prompt) {
        return gptService.getGPTResponse(prompt);
    }
}
