# Bokennoneko ![Icon](https://www.img.in.th/images/c3bb96845b9a7b47aff67323a6f9857d.png "Icon")
> ### *204113 [Principles of Computing] Project Chiang Mai University Computer Science 2019*

Bokennoneko เป็นโปรเจคที่กลุ่มนักศึกษามหาวิทยาลัยเชียงใหม่ชั้นปีที่ 1 ปีการศึกษา 2561 ทำขึ้นมาใน รายวิชา 204113 [Principles of Computing] เป็นเกมแนว Endless Run มีการนำรูปแบบ Flappy Bird + Jetpack Joyride มาประยุกต์ ให้มีสิ่งกีดขวางที่ยากขึ้น ระบบลดเลือดและลดเลือด เมื่อชนสิ่งกีดขวาง โดยความเร็วจะเพิ่มขึ้นตลอดตามระยะทาง

## การติดตั้งเกม
> เกมนี้รองรับเฉพาะระบบปฏิบัติการ Android version 4.2 (API 17) ขึ้นไป

![Download](https://www.img.in.th/images/68e55616020959e39fbf11e8c172bf43.jpg "Download")
1. Download เกมทาง Github
หรือ [Google Drive Download](https://drive.google.com/file/d/19SXdU0oJ_uxbHGH8FvM1848MD1_8pYvz/view?usp=sharing)
2. ติดตั้งไฟล์ bokennoneko.apk **(ถ้ามีปัญหาในการติดตั้งให้ทำการปิดระบบ google play protect แล้วลองติดตั้งอีกครั้ง)**
3. กดเข้าเกม แล้วเลือกรูปแบบการเข้าสู่ระบบ

<p align="center">
  <img src="https://www.img.in.th/images/c5370cf6e87a91f65ff94c8fd288d9f6.png"/>
</p>

## การใช้งาน
> ### **เกมนี้มีการเชื่อมต่อทาง Firebase**

- ระบบสมัครสมาชิก
  - เกมนี้สามารถสมัครเพื่อสามารถเล่นเกมได้
  - เกมนี้รองรับการสมัครผ่าน Google และ Facebook

    ![Login](https://www.img.in.th/images/18c4bd6d27d11bd44ab2f46e7739374f.png "Login")

- ระบบเก็บ Score
  - มีการเก็บ Max Score แต่ละตา โดยทุกๆคนสามารถเห็นลำดับ Max Score ของทุกคนได้
    
    ![Score](https://www.img.in.th/images/ebbed55d7c8d7d84ab8f91eda2601d3f.png "Score")
    
## วิธีเล่นเกม
![Start](https://www.img.in.th/images/c8b0941f6fb83cdb2da6f5cf1a035389.png "Start") ![Pause](https://www.img.in.th/images/66eda07914b66d9636ca88548c57f9e7.png "Pause") ![Over](https://www.img.in.th/images/b5a1a20c7f8e2ba0cfbfabde1c446bb2.png "Over")
> #### Interface
- โดยเมื่อกดปุ่มเล่น โดยจะมี 3 ชีวิตตามจำนวนตัวแมวทางด้านบนขวา
- ปุ่ม Pause สามารถกดหยุดเกมได้
  - ปุ่ม resume เพื่อเล่นต่อ
  - ปุ่ม restart เพื่อเล่นใหม่
  - ปุ่ม home เพื่อย้อนกลับไปหน้าแรก
- เมื่อไม่มีเลือดแล้วจะ Game Over ทันที!

> #### Controller
 - แตะเพิ่อกระโดด โดยมีแรงดึงดูดเมื่อปล่อยนิ้ว
 - กดหน้าจอค้างเพื่อบิน
 
> #### Trap and Item
- ![Trap](https://www.img.in.th/images/5faf55eca4bbbcfebe3a1d085395b4e7.png "Trap") เมื่อโดนก้างปลาแล้วชีวิตจะโดดลดไป 1 ชีวิต
  ```
  เมื่อโดนก้างปลาตัวแมวจะเป็นสีแดง และเมื่อแมวกระพริบจะอมตะประมาณ 2 วินาที
  ```
- ![Item](https://www.img.in.th/images/07e09bd2e496bd84e51fcac00bae512f.png "Item") เมื่อกินแซลมอลชีวิตจะเพิ่ม 1 ชีวิต
  ```
  ต้องมีเลือดน้อยกว่า 3 แซลมอลจึงจะปรากฏ
  ```
  
## อยากจะพัฒนาเกมต่อ?
> ควรทำความเข้าใจการ clone ผ่านทาง android studio เพื่อง่ายต่อการดึงข้อมูลมาพัฒนาต่อ
- แอพของเรา สามารถนำไปเขียนต่อเพื่อพัฒนาได้ทุกๆคน
 
  **URL for clone**
  ```
  https://github.com/WHIZENx/Bokennoneko
  ```
  **เปลี่ยนระบบ account Firebase**
  1. ให้ไปที่ไฟล์ Bokennoneko\app\
  2. ดาวน์โหลด google-services.json ทาง Firebase ของคุณมาทับได้เลย

## Contributors
> **ย่างเข้าเดือนห๊ก ฝนก็ต๊กปรอยๆ**
* 610510808	นายพิชัย	นามวรรณ์
* 610510649	นายณัฐภัทร	ปินตา
* 610510689	นายธนัทเทพ	สีดาบุตร
* 610510711	นายสหัสวรรษ	อ่อนศรี
* 610510680	น.ส.จารุภัทร	ชัยรักไพบูลย์กิจ
* 610510681	นายชนะวรรษ	จันทะคู่

![Poster](Poster.jpg "Poster")
