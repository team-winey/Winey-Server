package org.winey.server.controller.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateFeedRequestDto {
    @NotNull
    private String feedTitle;
    @NotNull
    private MultipartFile feedImage;
    @NotNull
    private Long feedMoney;
}
