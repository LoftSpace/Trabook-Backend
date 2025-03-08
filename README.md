# TraBook
여행 계획 공유 플랫폼
사용자는 본인의 여행계획을 만들고 커뮤니티에 업로드 할 수 있다.
사용자는 다른사람의 여행계획에 좋아요,스크랩,댓글 기능을 수행할 수 있다.

# 기술 스택
Backend : Java17, Spring Boot  

DevOps : MySQL, Redis, GCP

# 인프라 아키텍처
<img width="765" alt="스크린샷 2025-03-08 오후 6 18 06" src="https://github.com/user-attachments/assets/839e081f-1dcd-49cf-850a-1a99ec675b3d" width="300" height="300" />

# 문제 해결 및 성능 개선
https://github.com/LoftSpace/Trabook-Backend/wiki
1. 인기 게시글 목록을 위한 개선
2. 분산락을 통한 비 인기글 좋아요 동시제어
3. 댓글의 각 유저 정보 받아오기 : 외부 서버(유저관리 서버)와 상호작용시, 유저 수에 따라 api 대기 시간 증가
4. 외부 서버(유저 관리 서버)와 상호작용시, deserialization 도중 에러 해결
5. 조인과 인덱스를 통한 속도 개선
   

# ERD
![image (1)](https://github.com/user-attachments/assets/a0ece41a-edc8-49b1-bfa1-fec7def210c5)


