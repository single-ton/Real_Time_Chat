import com.microsoft.playwright.*;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.hyperskill.hstest.testcase.CheckResult.correct;
import static org.hyperskill.hstest.testcase.CheckResult.wrong;

public class ChatTests extends SpringTest {
    final static Random random = new Random();
    final static Pattern dateLengthPattern = Pattern.compile(".{5,}");
    final static Pattern overflowPattern = Pattern.compile("^(auto|scroll)$");
    final static int TIMEOUT = 10_000;
    final static int NUM_OF_PUBLIC_MESSAGES = 8;
    final static String URL = "http://localhost:28852";
    final static String TITLE = "Chat";

    final static String INPUT_MSG_ID_SELECTOR = "#input-msg";
    final static String INPUT_USERNAME_ID_SELECTOR = "#input-username";
    final static String SEND_MSG_BTN_ID_SELECTOR = "#send-msg-btn";
    final static String SEND_USERNAME_BTN_ID_SELECTOR = "#send-username-btn";
    final static String MESSAGES_ID_SELECTOR = "#messages";
    final static String MESSAGE_CONTAINER_CLASS_SELECTOR = ".message-container";
    final static String SENDER_CLASS_SELECTOR = ".sender";
    final static String MESSAGE_CLASS_SELECTOR = ".message";
    final static String DATE_CLASS_SELECTOR = ".date";
    final static String USERS_ID_SELECTOR = "#users";
    final static String USER_CONTAINER_CLASS_SELECTOR = ".user-container";
    final static String USER_CLASS_SELECTOR = ".user";
    final static String NEW_MESSAGE_COUNTER_CLASS_SELECTOR = ".new-message-counter";
    final static String CHAT_WITH_ID_SELECTOR = "#chat-with";
    final static String PUBLIC_CHAT_ID_BTN_SELECTOR = "#public-chat-btn";
    final static String INCORRECT_OR_MISSING_TITLE_TAG_ERR = "tag \"title\" should have correct text";

    final static String[] USERNAMES = {
            "A_USER_" + random.nextInt(1000, 10000),
            "B_USER_" + random.nextInt(1000, 10000),
            "C_USER_" + random.nextInt(1000, 10000),
    };

    final static String PUBLIC_CHAT = "Public chat";
    final static String[] RANDOM_PUBLIC_MESSAGES = Stream
            .generate(ChatTests::generateRandomMessage)
            .limit(NUM_OF_PUBLIC_MESSAGES)
            .toArray(String[]::new);

    final static String[] RANDOM_AC_MESSAGES = Stream
            .generate(ChatTests::generateRandomMessage)
            .limit(2)
            .toArray(String[]::new);

    final static String[] RANDOM_BC_MESSAGES = Stream
            .generate(ChatTests::generateRandomMessage)
            .limit(3)
            .toArray(String[]::new);

    final static List<String[]> NO_MESSAGES = List.of();
    final static List<String[]> sentMessagesWithSenders_PublicChat = new ArrayList<>();
    final static List<String[]> sentMessagesWithSenders_AC_Chat = new ArrayList<>();
    final static List<String[]> sentMessagesWithSenders_BC_Chat = new ArrayList<>();
    final List<Map.Entry<String, Integer>> usersAndNumOfNewMessages_A_User = new ArrayList<>();
    final List<Map.Entry<String, Integer>> usersAndNumOfNewMessages_B_User = new ArrayList<>();
    final List<Map.Entry<String, Integer>> usersAndNumOfNewMessages_C_User = new ArrayList<>();

    Playwright playwright;
    Browser browser;
    List<Page> pages = new ArrayList<>();

    @Before
    public void initBrowser() {
        playwright = Playwright.create();

        browser = playwright.firefox().launch(
                new BrowserType
                        .LaunchOptions()
                        .setHeadless(false)
//                       .setSlowMo(15)
                        .setTimeout(1000 * 120));
    }

    @After
    public void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    // Helper functions
    static Page openNewPage(String url, Browser browser, int defaultTimeout) {
        Page page = browser.newContext().newPage();
        page.navigate(url);
        page.setDefaultTimeout(defaultTimeout);
        return page;
    }

    static String generateRandomMessage() {
        return "Test message " + random.nextInt();
    }

    static CheckResult closePage(Page page) {
        page.close();
        return correct();
    }

    // Tests

    @DynamicTest
    DynamicTesting[] dt = new DynamicTesting[]{

            // --- LOGIN TESTS WITH TWO USERS
            // user 0
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testShouldContainProperTitle(pages.get(0), TITLE),
            () -> testElementShouldBeHidden(pages.get(0), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), USERS_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), CHAT_WITH_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testFillInputField(pages.get(0), USERNAMES[0], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                usersAndNumOfNewMessages_B_User.add(Map.entry(USERNAMES[0], 0));
                usersAndNumOfNewMessages_C_User.add(Map.entry(USERNAMES[0], 0));
                return testPressBtn(pages.get(0), SEND_USERNAME_BTN_ID_SELECTOR);
            },
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testChatWith(pages.get(0), PUBLIC_CHAT),

            // user 1
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testShouldContainProperTitle(pages.get(1), TITLE),
            () -> testElementShouldBeHidden(pages.get(1), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), USERS_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), CHAT_WITH_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testFillInputField(pages.get(1), USERNAMES[1], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                usersAndNumOfNewMessages_A_User.add(Map.entry(USERNAMES[1], 0));
                usersAndNumOfNewMessages_C_User.add(Map.entry(USERNAMES[1], 0));
                return testPressBtn(pages.get(1), SEND_USERNAME_BTN_ID_SELECTOR);
            },
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testChatWith(pages.get(1), PUBLIC_CHAT),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),

            // --- CHAT TESTS WITH TWO USERS
            () -> testElementShouldBeHidden(pages.get(0), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), SEND_USERNAME_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), SEND_USERNAME_BTN_ID_SELECTOR),
            // public message 0
            () -> testFillInputField(pages.get(0), RANDOM_PUBLIC_MESSAGES[0], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[0], RANDOM_PUBLIC_MESSAGES[0]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),

            // public message 1
            () -> testFillInputField(pages.get(1), RANDOM_PUBLIC_MESSAGES[1], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[1], RANDOM_PUBLIC_MESSAGES[1]});
                return testPressBtn(pages.get(1), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),

            // public message 2
            () -> testFillInputField(pages.get(0), RANDOM_PUBLIC_MESSAGES[2], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[0], RANDOM_PUBLIC_MESSAGES[2]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            // public message 3
            () -> testFillInputField(pages.get(1), RANDOM_PUBLIC_MESSAGES[3], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[1], RANDOM_PUBLIC_MESSAGES[3]});
                return testPressBtn(pages.get(1), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            // public message 4
            () -> testFillInputField(pages.get(0), RANDOM_PUBLIC_MESSAGES[4], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[0], RANDOM_PUBLIC_MESSAGES[4]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),


            // --- TESTS WITH THREE USERS
            // user 2
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testElementShouldBeHidden(pages.get(2), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), USERS_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), CHAT_WITH_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testFillInputField(pages.get(2), USERNAMES[2], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                usersAndNumOfNewMessages_A_User.add(Map.entry(USERNAMES[2], 0));
                usersAndNumOfNewMessages_B_User.add(Map.entry(USERNAMES[2], 0));
                return testPressBtn(pages.get(2), SEND_USERNAME_BTN_ID_SELECTOR);
            },

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),
            () -> testChatWith(pages.get(2), PUBLIC_CHAT),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),

            () -> testElementShouldBeHidden(pages.get(2), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), SEND_USERNAME_BTN_ID_SELECTOR),

            // public message 5
            () -> testFillInputField(pages.get(2), RANDOM_PUBLIC_MESSAGES[5], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[2], RANDOM_PUBLIC_MESSAGES[5]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_PublicChat),

            // --- TEST SWITCHING CHATS
            () -> testPressBtn(pages.get(0), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(0), USERNAMES[1]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), NO_MESSAGES),
            () -> testPressBtn(pages.get(0), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(0), USERNAMES[2]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), NO_MESSAGES),
            () -> testPressBtn(pages.get(0), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(0), PUBLIC_CHAT),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),

            () -> testPressBtn(pages.get(1), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(1), USERNAMES[2]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), NO_MESSAGES),

            // public message 6
            () -> testFillInputField(pages.get(2), RANDOM_PUBLIC_MESSAGES[6], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[2], RANDOM_PUBLIC_MESSAGES[6]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_PublicChat),

            // public message 7
            () -> testFillInputField(pages.get(2), RANDOM_PUBLIC_MESSAGES[7], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_PublicChat.add(new String[]{USERNAMES[2], RANDOM_PUBLIC_MESSAGES[7]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_PublicChat),


            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), NO_MESSAGES),
            () -> testChatWith(pages.get(1), USERNAMES[2]),
            () -> testPressBtn(pages.get(1), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(1), PUBLIC_CHAT),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),

            // --- PRIVATE MESSAGES TESTS
            () -> testPressBtn(pages.get(0), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(0), USERNAMES[2]),
            () -> testPressBtn(pages.get(1), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(1), USERNAMES[0]),
            () -> testPressBtn(pages.get(2), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(2), USERNAMES[0]),

            // private message 0
            () -> testFillInputField(pages.get(0), RANDOM_AC_MESSAGES[0], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_AC_Chat.add(new String[]{USERNAMES[0], RANDOM_AC_MESSAGES[0]});
                usersAndNumOfNewMessages_A_User.add(0, usersAndNumOfNewMessages_A_User.remove(1));
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), NO_MESSAGES),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_AC_Chat),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),

            // private message 1
            () -> testPressBtn(pages.get(1), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(1), PUBLIC_CHAT),

            () -> testFillInputField(pages.get(2), RANDOM_AC_MESSAGES[1], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_AC_Chat.add(new String[]{USERNAMES[2], RANDOM_AC_MESSAGES[1]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_AC_Chat),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),

            // private message 2
            () -> testPressBtn(pages.get(2), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(2), USERNAMES[1]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), NO_MESSAGES),

            () -> testFillInputField(pages.get(2), RANDOM_BC_MESSAGES[0], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_BC_Chat.add(new String[]{USERNAMES[2], RANDOM_BC_MESSAGES[0]});
                usersAndNumOfNewMessages_B_User.add(0, Map.entry(usersAndNumOfNewMessages_B_User.remove(1).getKey(), 1));
                usersAndNumOfNewMessages_C_User.add(0, usersAndNumOfNewMessages_C_User.remove(1));
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_BC_Chat),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),

            // private message 3
            () -> testFillInputField(pages.get(2), RANDOM_BC_MESSAGES[1], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_BC_Chat.add(new String[]{USERNAMES[2], RANDOM_BC_MESSAGES[1]});
                usersAndNumOfNewMessages_B_User.add(0, Map.entry(usersAndNumOfNewMessages_B_User.remove(0).getKey(), 2));
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_BC_Chat),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),

            // private message 4
            () -> {
                usersAndNumOfNewMessages_B_User.add(0, Map.entry(usersAndNumOfNewMessages_B_User.remove(0).getKey(), 0));
                return testPressBtn(pages.get(1), USER_CLASS_SELECTOR + " >> nth=0");
            },
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testChatWith(pages.get(1), USERNAMES[2]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_BC_Chat),

            () -> testFillInputField(pages.get(2), RANDOM_BC_MESSAGES[2], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders_BC_Chat.add(new String[]{USERNAMES[2], RANDOM_BC_MESSAGES[2]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_BC_Chat),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_BC_Chat),

            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(2), usersAndNumOfNewMessages_C_User),

            // recheck chats/messages
            () -> testPressBtn(pages.get(0), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(0), USERNAMES[2]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_AC_Chat),
            () -> testPressBtn(pages.get(1), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(1), USERNAMES[2]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_BC_Chat),
            () -> testPressBtn(pages.get(2), USER_CLASS_SELECTOR + " >> nth=0"),
            () -> testChatWith(pages.get(2), USERNAMES[1]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_BC_Chat),

            () -> testPressBtn(pages.get(0), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(0), USERNAMES[1]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), NO_MESSAGES),
            () -> testPressBtn(pages.get(1), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(1), USERNAMES[0]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), NO_MESSAGES),
            () -> testPressBtn(pages.get(2), USER_CLASS_SELECTOR + " >> nth=1"),
            () -> testChatWith(pages.get(2), USERNAMES[0]),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_AC_Chat),


            () -> testPressBtn(pages.get(0), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(0), PUBLIC_CHAT),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders_PublicChat),
            () -> testPressBtn(pages.get(1), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(1), PUBLIC_CHAT),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders_PublicChat),
            () -> testPressBtn(pages.get(2), PUBLIC_CHAT_ID_BTN_SELECTOR),
            () -> testChatWith(pages.get(2), PUBLIC_CHAT),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders_PublicChat),


            // --- TEST STATE AFTER CLOSING PAGE
            () -> {
                usersAndNumOfNewMessages_A_User.remove(0);
                usersAndNumOfNewMessages_B_User.remove(0);
                return closePage(pages.get(2));
            },
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(1), usersAndNumOfNewMessages_B_User),
            () -> {
                usersAndNumOfNewMessages_A_User.remove(0);
                return closePage(pages.get(1));
            },
            () -> testUserListShouldHaveProperStructureAndContent(pages.get(0), usersAndNumOfNewMessages_A_User),
    };

    CheckResult testShouldContainProperTitle(Page page, String title) {
        return title.equals(page.title()) ? correct() : wrong(INCORRECT_OR_MISSING_TITLE_TAG_ERR);
    }

    CheckResult testElementShouldBeHidden(Page page, String elemSelector) {
        try {
            assertThat(page.locator(elemSelector)).isHidden();
            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testFillInputField(Page page, String msg, String inputFieldSelector) {
        try {
            assertThat(page.locator(inputFieldSelector)).isEmpty();
            page.fill(inputFieldSelector, msg);
            return correct();
        } catch (PlaywrightException | AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testPressBtn(Page page, String btnSelector) {
        try {
            page.click(btnSelector);
            return correct();
        } catch (PlaywrightException e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testUserListShouldHaveProperStructureAndContent(Page page, List<Map.Entry<String, Integer>> usersAndNumOfNewMessages){

        Locator usersLocator = page.locator(USERS_ID_SELECTOR);
        Locator userContainersLocator = usersLocator.locator(USER_CONTAINER_CLASS_SELECTOR);

        try {
            assertThat(usersLocator).hasCSS("overflow-y", overflowPattern);
            assertThat(userContainersLocator).hasCount(usersAndNumOfNewMessages.size());

            for (int i = 0; i < usersAndNumOfNewMessages.size(); i++) {

                Locator userContainerLocator = userContainersLocator.nth(i);
                assertThat(userContainerLocator.locator(USER_CLASS_SELECTOR)).isVisible();
                assertThat(userContainerLocator.locator(USER_CLASS_SELECTOR)).hasCount(1);
                assertThat(userContainerLocator.locator(USER_CLASS_SELECTOR)).hasText(usersAndNumOfNewMessages.get(i).getKey());

                if (usersAndNumOfNewMessages.get(i).getValue() > 0) {
                    assertThat(userContainerLocator.locator(NEW_MESSAGE_COUNTER_CLASS_SELECTOR)).isVisible();
                    assertThat(userContainerLocator.locator(NEW_MESSAGE_COUNTER_CLASS_SELECTOR)).hasCount(1);
                    assertThat(userContainerLocator.locator(NEW_MESSAGE_COUNTER_CLASS_SELECTOR)).hasText(String.valueOf(usersAndNumOfNewMessages.get(i).getValue()));
                } else {
                    assertThat(userContainerLocator.locator(NEW_MESSAGE_COUNTER_CLASS_SELECTOR)).not().isVisible();
                }
            }

            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testUserMessagesShouldHaveProperStructureAndContent(Page page, List<String[]> sentMessagesWithSenders) {
        Locator messagesLocator = page.locator(MESSAGES_ID_SELECTOR);
        Locator messageContainersLocator = messagesLocator.locator(MESSAGE_CONTAINER_CLASS_SELECTOR);

        try {
            assertThat(messagesLocator).hasCSS("overflow-y", overflowPattern);
            assertThat(messageContainersLocator).hasCount(sentMessagesWithSenders.size());

            for (int i = 0; i < sentMessagesWithSenders.size(); i++) {
                Locator messageContainerLocator = messageContainersLocator.nth(i);

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).isVisible();
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).isVisible();
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).isVisible();

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).hasCount(1);
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).hasCount(1);
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).hasCount(1);

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).hasText(sentMessagesWithSenders.get(i)[0]);
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).hasText(sentMessagesWithSenders.get(i)[1]);
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).hasText(dateLengthPattern);
            }

            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testChatWith(Page page, String text) {
        try {
            assertThat(page.locator(CHAT_WITH_ID_SELECTOR)).hasText(text);
            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }
}