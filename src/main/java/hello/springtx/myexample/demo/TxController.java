package hello.springtx.myexample.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tx")
@RequiredArgsConstructor
public class TxController {

  private final AService aService;

  @GetMapping("/{type}")
  public void txTest(@PathVariable Integer type) {
    switch (type) {
      case 1: aService.aMethod(); return;
      case 2: aService.aMethodWithoutTryCatch(); return;
      case 3: aService.aMethodWithoutBMethodTryCatch(); return;
      case 4: aService.aMethodWithAMethodTryCatchWithoutBMethodTryCatch(); return;
      default:
        System.out.println("hello! Select one of the cases 1~4!!");
    }
  }
}
