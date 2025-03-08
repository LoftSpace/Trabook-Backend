# TraBook
여행 계획 공유 플랫폼
사용자는 본인의 여행계획을 만들고 커뮤니티에 업로드 할 수 있다.
사용자는 다른사람의 여행계획에 좋아요,스크랩,댓글 기능을 수행할 수 있다.

# 기술 스택
Backend : Java17, Spring Boot 
DevOps : MySQL, Redis, GCP

# 인프라 아키텍처
<img width="867" alt="image" src="https://github.com/user-attachments/assets/3ff98c88-6d30-46c1-a538-24121842b566" />

# 문제 해결 및 성능 개선
1. 인기 게시글 목록을 위한 개선
2. 분산락을 통한 비 인기글 좋아요 동시제어
3. 댓글의 각 유저 정보 받아오기 : 외부 서버(유저관리 서버)와 상호작용시, 유저 수에 따라 api 대기 시간 증가
4. 외부 서버(유저 관리 서버)와 상호작용시, deserialization 도중 에러 해결
5. 조인과 인덱스를 통한 속도 개선
   

# ERD


