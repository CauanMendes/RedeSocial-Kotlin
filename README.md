# RedeSocial-Kotlin

Aplicativo Android de rede social escrito em Kotlin. Permite cadastro/login, criação de posts com imagem e localização, feed paginado com busca por cidade e edição de perfil com foto.

## Funcionalidades

- **Cadastro e login** via Firebase Authentication (e-mail/senha).
- **Feed** de posts ordenado por data, com paginação infinita (5 posts por vez).
- **Busca** de posts por cidade.
- **Novo post** com imagem (galeria), texto e cidade detectada por GPS.
- **Perfil** editável: nome, username, foto e troca de senha.

## Stack

- Kotlin + Android SDK 33–35
- View Binding
- Firebase Authentication
- Cloud Firestore (coleções `usuarios` e `posts`)
- Google Play Services Location (FusedLocationProvider + Geocoder)

## Estrutura do projeto

Organização no padrão Android, separada por responsabilidade:

```
com.example.redesocialcauan/
├── ui/         Activities (Login, SignUp, Home, Profile, CreatePost)
├── auth/       UserAuth — wrapper de FirebaseAuth
├── dao/        PostDAO, UserDAO — acesso ao Firestore
├── model/      Post, User — data classes
├── adapter/    PostAdapter — RecyclerView do feed
└── util/       Base64Converter, LocalizacaoHelper
```

## Modelo de dados (Firestore)

**`usuarios/{email}`**
```
nomeCompleto: String
username:     String
fotoPerfil:   String (Base64 PNG 150x150)
```

**`posts/{autoId}`**
```
autor:        String (email)
texto:        String
cidade:       String
imagemBase64: String
timestamp:    Timestamp (server-side)
```

As fotos são gravadas em Base64 direto no documento (sem Firebase Storage) — comprimidas em PNG 150x150 por `Base64Converter`.

## Setup

1. Clonar o repositório.
2. Criar um projeto no [Firebase Console](https://console.firebase.google.com/) e baixar o `google-services.json` para `app/`.
3. Habilitar no console:
   - **Authentication** → método e-mail/senha.
   - **Firestore Database** em modo de teste (ou regras adequadas).
4. Abrir no Android Studio e rodar (`Shift+F10`).

> Requer `minSdk = 33`. Para testar geolocalização no emulador, use **Extended Controls → Location → SET LOCATION**.

## Permissões

Declaradas em `AndroidManifest.xml`:

- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — cidade do post
- `INTERNET` — Firebase

## Telas

| Tela | Activity | Layout |
|---|---|---|
| Login | `LoginActivity` | `activity_main.xml` |
| Cadastro | `SignUpActivity` | `activity_cadastro.xml` |
| Home | `HomeActivity` | `activity_home.xml` |
| Perfil | `ProfileActivity` | `activity_perfil.xml` |
| Novo Post | `CreatePostActivity` | `activity_novo_post.xml` |
