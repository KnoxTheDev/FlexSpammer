# FlexSpammer

**FlexSpammer** is a customizable [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) addon module designed for sending messages in Minecraft chat at configurable intervals. It introduces dynamic variations in each message to bypass spam filters and debouncing.

---

## 🌟 Features

- 🧠 **Flexible base message** — Customize what the module sends.
- ⏱️ **Adjustable delay** — Set your own time interval between messages (in milliseconds).
- 🔁 **Dynamic variation** — Each message includes a changing character to bypass anti-spam.
- 🎮 Built for **Minecraft 1.21.5** and Meteor Client.
- 🧩 Lightweight, modular, and easy to integrate.

---

## 🔧 How to Use

1. **Install Meteor Client.**
2. **Download the `FlexSpammer.jar` addon** from [Releases](https://github.com/KnoxTheDev/FlexSpammer/releases).
3. Move the `.jar` into:
   ```
   .minecraft/meteor-client/addons/
   ```
4. Start Minecraft with Meteor Client.

---

## 🛠️ Configuration

Once in-game:

1. Press `Right Shift` to open the Meteor GUI.
2. Search for `FlexSpammer` (under the **Misc** category).
3. Enable the module and customize:
   - **Base Message**: What will be sent.
   - **Delay**: How often (in ms) the message is sent.

Each message includes a rotating suffix from the ASCII set (`a` to `Z` to `0–9`) to avoid duplication and server-side debounce filters.

---

## 🧩 Module Details

**Module Name:** `Flex Spammer`  
**Category:** `Misc`  
**Description:** Sends a message repeatedly with a randomized suffix to bypass spam detection.

> Base functionality is in `FlexSpammer.java`, dynamically appending suffix characters from a predefined ASCII array.

---

## 📁 Project Structure

```
com.example.addon/
├── AddonTemplate.java        # Main addon class, registers the category and module
└── modules/
    └── FlexSpammer.java      # Main logic of the FlexSpammer module
```

---

## 👨‍💻 Developer Notes

To build from source:

```bash
./gradlew build
```

Output will be in:

```
build/libs/
```

Deploy the `.jar` to your Meteor Client `addons/` directory.

---

## 🧾 License

This project is licensed under the MIT License — see the [LICENSE](./LICENSE) file for details.

---

## 🤝 Credits

- Built with ❤️ on top of the Meteor Client addon framework
- Inspired by fun and flexibility for casual use

---

## 🔗 Repository

[https://github.com/KnoxTheDev/FlexSpammer](https://github.com/KnoxTheDev/FlexSpammer)
