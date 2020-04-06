# Educational visual programming language for NodeMCU
<img src="https://github.com/RobertoDebarba/educational-visual-programming-language-for-esp8266/blob/master/screenshots/scr1.png" width="250" width="auto">  
  
Although computing is present in all sectors of society today, there is a lack of knowledge and interest of the population in this area. One way of making scientific and technological knowledge viable and at the same time stimulate creativity and experimentation is with the use of educational robotics. With the objective of enabling access to robotics in the classroom, through hardware alternatives in relation to the products available on the market, looking to facilitate the teaching of programming logic and increase students' interest in the area, this work presents the development of a visual programming language based on blocks to support the teaching of programming logic in schools with the aid of educational robotics, using the NodeMCU Esp8266 microcontroller. This work also presents the Otto DIY project and its use, adapting its operation to NodeMCU.

*Plataforma de Programação Visual para NodeMCU*  
  
*Apesar da computação estar presente em todos os setores da sociedade hoje, existe uma carência de conhecimento e interesse da população nesta área. Uma forma de viabilizar o conhecimento científicotecnológico e ao mesmo tempo estimular a criatividade e a experimentação é a utilização da robótica educacional. Com o objetivo de possibilitar o acesso à robótica em sala de aula, através de alternativas de hardware em relação aos produtos existentes no mercado, buscando facilitar o ensino de lógica de programação e aumentar o interesse dos alunos pelo tema, este trabalho apresenta o desenvolvimento de uma plataforma para programação visual baseada em blocos para suporte ao ensino de lógica de programação nas escolas com o auxílio de robótica educacional, usando o microcontrolador NodeMCU Esp8266. Este trabalho também apresenta o projeto Otto DIY e sua utilização, adaptando seu funcionamento para o microcontrolador em questão.*  
  
**Read the complete [paper here](https://github.com/RobertoDebarba/educational-visual-programming-language-for-esp8266/blob/master/doc/paper.pdf).**
  
<img src="https://github.com/RobertoDebarba/educational-visual-programming-language-for-esp8266/blob/master/screenshots/scr2.png" width="800" width="auto">
<img src="https://github.com/RobertoDebarba/educational-visual-programming-language-for-esp8266/blob/master/screenshots/scr3.png" width="800" width="auto">

## How to run

### Frontend

* Double click on index.html
* Ready!

### Backend

**Requirements**
* OpenJDK 11
* Maven 3.5.3+
* PlatformIO CLI
* Set enviroment variables: 
    * AWS_REGION=us-east-1
    * AWS_ACCESS_KEY_ID=?
    * AWS_SECRET_ACCESS_KEY=?
* Set on application.properties:
    * aws.s3.bucketname
* Copy firmware folder to ~/educational-visual-programming-language-for-esp8266/" whith name "source"

**Run**
* cmd "./mvnw compile quarkus:dev"

### Firmware

**Requirements**
Some libraries in lib folder were copy from Otto DIY project because it was not versioned =(
I created a patch with the changes I made to run on Esp8266: ./firmware/lib/ottodiylibs.patch

**Run**
* PlatformIO CLI
* Set on platformio.ini:
    * upload_port 
* Set on secrets.h:
    * S3_BUCKET
    * S3_FINGETPRINT

**Write firmware**
* Copy the file "example/bootstrap.cpp" to "src/src.ino"
* cmd "platformio run -t upload"

## Authors

[Roberto Luiz Debarba](https://github.com/RobertoDebarba)

## License

The codebase is licensed under [GPL v3.0](http://www.gnu.org/licenses/gpl-3.0.html).
