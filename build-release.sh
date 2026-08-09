#!/bin/bash
set -e

echo "🔨 Начинаю сборку Release APK..."

# Шаг 1: Создать подписной ключ
echo "📝 Создаю подписной ключ..."
keytool -genkey -v -keystore my-upload-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias upload \
  -storepass android123 \
  -keypass android123 \
  -dname "CN=PaperAccounting, OU=Development, O=PrintShop, L=Moscow, S=Moscow, C=RU" \
  -noprompt

# Шаг 2: Собрать Release APK
echo "⚙️ Собираю Release APK..."
KEYSTORE_PATH="$(pwd)/my-upload-key.jks" \
STORE_PASSWORD="android123" \
KEY_PASSWORD="android123" \
./gradlew assembleRelease

# Шаг 3: Проверить результат
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
  echo ""
  echo "✅ Сборка успешна!"
  echo "📦 APK готов: $(pwd)/app/build/outputs/apk/release/app-release.apk"
  echo "💾 Keystore сохранён: $(pwd)/my-upload-key.jks"
  ls -lh app/build/outputs/apk/release/app-release.apk
else
  echo "❌ Ошибка при сборке APK"
  exit 1
fi
