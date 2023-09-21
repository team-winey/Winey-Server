package org.winey.server.slack;

import com.slack.api.Slack;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.webhook.WebhookPayloads;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.slack.api.model.block.composition.BlockCompositions.plainText;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlackApi {

    @Value("${slack.webhook.url}")
    private String webhookUrl;
    private final static String NEW_LINE = "\n";
    private final static String DOUBLE_NEW_LINE = "\n\n";

    private StringBuilder sb = new StringBuilder();

    public void sendAlert(Exception error, HttpServletRequest request) throws IOException {

        List<LayoutBlock> layoutBlocks = generateLayoutBlock(error, request);

        Slack.getInstance().send(webhookUrl, WebhookPayloads
                .payload(p ->
                        p.username("Exception is detected 🚨")
                                .iconUrl("https://yt3.googleusercontent.com/ytc/AGIKgqMVUzRrhoo1gDQcqvPo0PxaJz7e0gqDXT0D78R5VQ=s900-c-k-c0x00ffffff-no-rj")
                                .blocks(layoutBlocks)));
    }

    private List<LayoutBlock> generateLayoutBlock(Exception error, HttpServletRequest request) {
        return Blocks.asBlocks(
                getHeader("서버 측 오류로 예상되는 예외 상황이 발생하였습니다."),
                Blocks.divider(),
                getSection(generateErrorMessage(error)),
                Blocks.divider(),
                getSection(generateErrorPointMessage(request)),
                Blocks.divider(),
                getSection("<https://github.com/team-winey/Winey-Server/issues|이슈 생성하러 가기>")
        );
    }

    private String generateErrorMessage(Exception error) {
        sb.setLength(0);
        sb.append("*[🔥 Exception]*" + NEW_LINE + error.toString() + DOUBLE_NEW_LINE);
        sb.append("*[📩 From]*" + NEW_LINE + readRootStackTrace(error) + DOUBLE_NEW_LINE);

        return sb.toString();
    }

    private String generateErrorPointMessage(HttpServletRequest request) {
        sb.setLength(0);
        sb.append("*[🧾세부정보]*" + NEW_LINE);
        sb.append("Request URL : " + request.getRequestURL().toString() + NEW_LINE);
        sb.append("Request Method : " + request.getMethod() + NEW_LINE);
        sb.append("Request Time : " + new Date() + NEW_LINE);

        return sb.toString();
    }
    private String generateReportMessage(Feed feed){
        sb.setLength(0);
        sb.append("*[🔥 feed id]*" + NEW_LINE + feed.getFeedId() + DOUBLE_NEW_LINE);
        sb.append("*[📩 feed content]*" + NEW_LINE + feed.getFeedTitle() + DOUBLE_NEW_LINE);
        sb.append("*[🚨 feed image]*" + NEW_LINE + feed.getFeedImage() + DOUBLE_NEW_LINE);
        return sb.toString();
    }



    private String readRootStackTrace(Exception error) {
        return error.getStackTrace()[0].toString();
    }

    private LayoutBlock getHeader(String text) {
        return Blocks.header(h -> h.text(
                plainText(pt -> pt.emoji(true)
                        .text(text))));
    }

    private LayoutBlock getSection(String message) {
        return Blocks.section(s ->
                s.text(BlockCompositions.markdownText(message)));
    }
    public String sendReport(User user, Feed feed) throws IOException{
        List<LayoutBlock> layoutBlocks = generateReportBlock(feed);
        Slack.getInstance().send(webhookUrl, WebhookPayloads
                .payload(p ->
                        p.username("신고가 접수되었습니다. 🚨")
                                .iconUrl("https://yt3.googleusercontent.com/ytc/AGIKgqMVUzRrhoo1gDQcqvPo0PxaJz7e0gqDXT0D78R5VQ=s900-c-k-c0x00ffffff-no-rj")
                                .blocks(layoutBlocks)));
        return "신고가 정상적으로 접수되었습니다.";
    }

    private List<LayoutBlock> generateReportBlock(Feed feed) {
        return Blocks.asBlocks(
                getHeader("신고가 들어온 게시물이 있습니다.🚨"+NEW_LINE+"정신적으로 해로운 사진일 수 있으니 주의하세요."),
                Blocks.divider(),
                getSection(generateReportMessage(feed)),
                Blocks.divider(),
                getSection("<https://github.com/team-winey/Winey-Server/issues|이슈 생성하러 가기>")
        );
    }
}