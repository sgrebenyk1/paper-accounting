# Инструкция по сборке APK-файла (Учет Бумаги)

В проект добавлены скрипты автоматической сборки и настроена автоматически работа с GitHub Actions.

## Способы получения APK-файла:

### 1. Автоматическая сборка в GitHub (Самый простой способ)
1. Выполните Push / Экспорт проекта в ваш репозиторий GitHub.
2. Перейдите во вкладку **Actions** в вашем репозитории на GitHub.
3. Выберите запуск рабочей области **Build Android APK**.
4. После окончания сборки внизу страницы появится раздел **Artifacts**, где вы сможете в 1 клик скачать готовые файлы:
   - `paper-inventory-release-apk` (Готовый Release APK)
   - `paper-inventory-debug-apk` (Debug APK)

---

### 2. Сборка с помощью добавленных скриптов (Локально)

#### На Windows:
1. Откройте папку с проектом.
2. Запустите двойным кликом файл `build_apk.bat`.
3. После завершения готовые APK будут находиться по пути:
   - `app\build\outputs\apk\release\app-release.apk`
   - `app\build\outputs\apk\debug\app-debug.apk`

#### На macOS / Linux:
1. Откройте терминал в папке проекта.
2. Запустите команды:
   ```bash
   chmod +x build_apk.sh
   ./build_apk.sh
   ```
3. Готовые APK будут сохранены в:
   - `app/build/outputs/apk/release/app-release.apk`
   - `app/build/outputs/apk/debug/app-debug.apk`

---

### 3. Сборка через Android Studio
1. Откройте проект в Android Studio.
2. В верхнем меню выберите: **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)**.
3. После окончания сборки в правом нижнем углу появится уведомление со ссылкой **locate**, кликнув по которой вы попадете в папку с APK-файлом.
