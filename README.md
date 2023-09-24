# Управление каталогами на основе разработанного командного языка  
## Задание: создать консольное приложение для управления каталоги с помошью командного языка.  
**Командный язык** - это тип интерпретируемого языка, использующий структуру командной строки.  
_Режимы работы программы_:  
**TestDir** - основной каталог для выполнения задания, который находится в корне проекта  
  
_Основне команды:_
* view - показывает все файлы и каталоги(отмечены[dir]) в текущей папке  
* cd
   - C://Games - перемещение в каталог по абсолютному пути       
   - /65 - перемещение в каталог, который находится в TestDir
* copy - копирование файло и каталогов по имени или расширению
     - -byname - копирование по имени файла или каталога
          - name.txt C://Games - копирование файла по абсолютному пути
          - dirName /newDir - копирование всей директории с файлами в папку newDir(копирование папки в себя не поддерживается)
     - -byext
          - .txt D://Example - копирование всех файлов с расширение .txt по абсолютному пути
          - -byname dir /example - копирование всех директрий с их файлами в директрию example(копирование папки в себя не поддерживается)
* !help - вывод всех доступных команд
* copyalg - отображение алгоритма работы с функцией copy
* exit - выход из программы

