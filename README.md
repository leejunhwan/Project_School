# 학교이름 빈도찾기

### [개발환경]
 * Java8
 * Gradle
 

### [과제 요구사항]
 * comment 문서안에서 학교이름을 최대한 찾아서 빈도수와 함께 result.txt 에 저장
 * 저장은 '학교이름 개수' 꼴로 개행 처리하여 저장



### [실행 과정]
* 1. 대한민국 학교정보 리스트 조회 - 커리어넷 오픈 API 를 활용하여 (초,중,고,대,특수,대안) 학교 정보를 모두 호출 (가끔, 커넥션 오류로 인해 끊기는 현상 간헐 적으로 발생 )
* 2. 프로젝트의 resources/comments 에 저장되어있는 원본 데이터를 변형 (특수문자, 공백, 개행 제거) 를 통해 String 변수로 변환
* 3. 1번에서 가지고온 학교정보 리스트에 학교정보를 변형 ( ex> 전남여자상업고등학교 -> {전남여자상업고, 전남여자상고, 전남여상업고, 전남여상} ) 
* 4. 3번을 통해 변형된 학교이름 리스트를 KMP 문자열 검색 알고리즘을 통해 각각 개수를 더하여 결과 리스트에 저장
* 5. 결과 리스트를 result.txt 파일에 write



### [문제 해결과정]
##### 최초 학교와 관련되 postfix (학교, 고, 중, 대, 초 ...)를 전부 찾아내어 이를 기반으로 검색 하려고 했으나, 
##### "탕수육중먹고싶어요" 라는 글 같은 경우에 탕수육중을 중학교로 판단해야하는데 무리가 존재
##### 대한민국의 모든 학교정보를 먼저 알고 이를 기반으로 검색을 하는것이 데이터에 설득력이 있다고 판단하여, 커리어넷 오픈 API를 활용하여 학교정보를 찾아옴
##### comments 내의 학교이름이 정확히 '광주숭일고등학교' 꼴로 등장하지 않기 때문에 출현이 가능할 만한 모든 후보들이 필요하며, 이 후보들을 잘 걸러내는 것이 알고리즘의 key
##### 이후 문자열 탐색 알고리즘 KMP, 보이어무어 두가지를 검토했고 KMP알고리즘을 선택하여 학교이름 후보군들의 출현개수를 합산



### [검색이 안되는 케이스]
##### 커리어넷에 존재하지 않는 학교 (ex. 서울연희미용고등학교, 세인트폴 학교)



### [검색데이터의 오류 케이스]
##### 소하고등학교 의 경우 소하고 22개가 출현을 하는데, '맛이 고소하고~ ' 라는 글자를 소하고등학교로 판단하는 오류
##### 대안학교 이름이슈 : 대안학교의 이름의 경우는 규칙이 없는 경우가 많아, '이랑학교' 의 경우 36개가 출현을 하는데, '선생님이랑 학교' 라는 글자를 이랑학교라고 판단하는 오류 (commnets를 변경하는 시점에 공백을 제거하였기 때문에 발생)
##### 대구대학교 26개가 출현하는데 대구대구여자고등학교를 대구대로 판단하는 오류
