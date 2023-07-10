# Winey-Server
## 👩🏻‍💻 팀원 소개

<table>
    <tr align="center">
              <td style="min-width: 200px;">
            <a href="https://github.com/hayounSong">
              <img src="https://github.com/funnysunny08.png" width="100">
              <br />
              <b>전선희 (funnysunny08)</b>
            </a>
                       <br/>
              Server
        </td>
        <td style="min-width: 200px;">
            <a href="https://github.com/sss4920">
              <img src="https://github.com/sss4920.png" width="100">
              <br />
              <b>김수현(sss4920)</b>
            </a> 
            <br/>
       
 Server
    </tr>

</table>

협업 노션: https://empty-weaver-a9f.notion.site/Server-ba05f43a9dfe44de969af9a27cea4481?pvs=4

## 📱 합리적인 소비를 하고싶지만 실패하는 사람들을 위한 서비스, 위니(Winey)!! 

### 🧐 마이페이지 API
현재 내가 절약해서 나의 레벨과 정보들을 조회할 수 있는 API를 제공합니다.

### 🍓 목표 설정 API
내가 절약하고 싶은 금액을 설정하는 API 제공

### 💊 절약 추천법 API
다양한 경로에서 절약을 할 수 있는 절약 방법들을 추천하는 API를 제공합니다.

### 😍 피드 좋아요/취소 API
다른사람들이 만든 피드들에 좋아요를 남기는 좋아요 기능 API 제공

### 👀 피드 전체 불러오기 API
모두가 절약한 내용들을 한 눈에 보는 전체조회 API 제공

### 🌱 피드 생성 API
내가 절약한 내용들을 기록하는 피드 생성 API 제공

### 🌼 마이 피드, 마이 피드 삭제 API
내가 작성한 피드들을 조회하고, 삭제할 수 있는 API 제공

<br />

![image](https://github.com/team-winey/Winey-Server/assets/49307946/d6985b76-98a1-4ff4-a3ef-4629cc3cd7a1)


<hr/>
<br />


## 🛠 기술 스택


### 🖥 Backend

|역할|종류|
|-|-|
|Framework|<img alt="RED" src ="https://img.shields.io/badge/SPRING-6DB33F.svg?&style=for-the-badge&logo=Spring&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/SPRING Boot-6DB33F.svg?&style=for-the-badge&logo=SpringBoot&logoColor=white"/>|
|Database|<img alt="RED" src ="https://img.shields.io/badge/Mysql-003545.svg?&style=for-the-badge&logo=Mysql&logoColor=white"/>|
|Database Service|<img alt="RED" src ="https://img.shields.io/badge/Amazon Rds-527FFF.svg?&style=for-the-badge&logo=AmazonRds&logoColor=white"/>|
|Programming Language|<img alt="RED" src ="https://img.shields.io/badge/JAVA-004027.svg?&style=for-the-badge&logo=Jameson&logoColor=white"/>|
|API|![REST](https://img.shields.io/badge/Rest-4B3263?style=for-the-badge&logo=rest&logoColor=white)                                     
|Version Control|![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white) |
|CI/CD|![Jenkins](https://img.shields.io/badge/Jenkins-%D24939.svg?style=for-the-badge&logo=Jenkins&logoColor=white)|
<br />


### 🖥 Common
|역할|종류|
|-|-|
|협업 관리|<img alt="RED" src ="https://img.shields.io/badge/Notion-000000.svg?&style=for-the-badge&logo=Notion&logoColor=white"/> |
|디자인|<img alt="RED" src ="https://img.shields.io/badge/Figma-F24E1E.svg?&style=for-the-badge&logo=Figma&logoColor=white"/>|

<br />


## 📂 폴더 구조
```

├── 📂.github

├── 📂 main
	├── 🗂️ resources
		├── 📕 application.yml


	├── 📂 controller(컨트롤러 파일)
		├── 🗂️ dto
		 ├──🗂️ request
		 ├──🗂️ response
			
	├── 📂 domain(엔티티 파일)

	├── 📂 infrastructure(레포지토리 폴더)
	
	├── 📂 service(서비스 파일)
	
	├── 📂 exception(Exception enum, Exception class 파일)
		├── 🗂️ model

	├── 📂 config(swagger 관련 파일)

	├── 📂 exception(Exception enum, Exception class 파일)

	├── 📂 external(S3 service 파일)
      ├── 🗂️ client
          ├── 🗂️ aws
		          ├── 📕 S3Service

	├── 📂 common(공용 클래스 관리)
		├──🗂️ advice
		├──🗂️ dto
```


# Code Convention

---

1. File Naming
- 파일 이름 및 클래스, 인터페이스 이름: **파스칼 케이스(Pascal Case)**

```java
public class ControllerExceptionAdvice {
```

1. Entity에서 사용되는 속성값들은 ? **카멜 케이스**(**camel Case)**

```java
private String email;
```

1. 내부에서 사용되는 함수 및 기타 사용: **카멜 케이스(camelCase)**

```java
public ApiResponse createEmail(@RequestBody @Valid final BoardRequestDto request) {
```

1. 엔티티 생성자 관리는 **Builder Pattern 사용**

```java
@Builder
```

### **인터페이스 이름에 명사/형용사 사용 [interface-noun-adj]**

인터페이스(interface)의 이름은 명사/명사절로 혹은 형용사/형용사절로 짓는다.

```java
// Good Exemples
public interface RowMapper {
public interface AutoClosable {
```

### **클래스 이름에 명사 사용 [class-noun]**

클래스 이름은 명사나 명사절로 짓는다.

### **메서드 이름은 동사/전치사로 시작 [method-verb-preposition]**

메서드명은 기본적으로 동사로 시작한다.

✔️ 다른 타입으로 전환하는 메서드나 빌더 패턴을 구현한 클래스의 메서드에서는 전치사를 쓸 수 있다.

```java
- 동사사용 : renderHtml()
- 전환메서드의 전치사 : toString()
- Builder 패턴 적용한 클래스의 메서드의 전치사 : withUserId(String id)
```

### 🍑 **상수**

### **상수는 대문자와 언더스코어로 구성[constant_uppercase]**

"static final"로 선언되어 있는 필드일 때 상수로 간주한다.

상수 이름은 대문자로 작성하며, 복합어는 언더스코어'_'를 사용하여 단어를 구분한다.

```java
public final int UNLIMITED = -1;
public final String POSTAL_CODE_EXPRESSION = “POST”;
```

### **변수**

### **변수에 소문자 카멜표기법 적용 [var-lower-camelcase]**

상수가 아닌 클래스의 멤버변수/지역변수/메서드 파라미터에는 소문자 카멜표기법(Lower camel case)을 사용한다.

```java
private boolean authorized;
private int accessToken;
```

### **임시 변수 외에는 1 글자 이름 사용 금지 [avoid-1-char-var]**

메서드 블럭 범위 이상의 생명 주기를 가지는 변수에는 1글자로 된 이름을 쓰지 않는다.

**반복문의 인덱스나 람다 표현식의 파라미터 등 짧은 범위의 임시 변수**에는 관례적으로 1글자 변수명을 사용할 수 있다.

```java
HtmlParser parser = new HtmlParser();
```
