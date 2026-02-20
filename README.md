# Auto Answer Mod

A Minecraft Fabric mod for version 1.21.4 that automatically solves math equations and answers custom prompts in chat.

## Features

- **✅ Automatic Math Solving**: Detects and solves mathematical equations in chat
  - Simple operations: `5+3`, `12*4`, `100-25`, `20/4`
  - Complex expressions: `(5+3)*2`, `10+5*2-3`, etc.
  - Supports parentheses and order of operations
  
- **✅ Custom Prompt Answering**: Automatically responds to your custom prompts
  - Edit `config/autoanswer.json` to add your prompts
  - Case-insensitive matching
  - Empty by default - you add what you need!

- **✅ Mod Menu Integration**: Easy in-game config access
  - Open config folder directly from Mod Menu
  - Reload config without restarting Minecraft
  - View how many prompts are loaded

- **✅ Toggle Button**: Quick enable/disable in bottom-right corner
  - Click the button to toggle the mod on/off
  - Green "AA: ON" when enabled, Red "AA: OFF" when disabled
  - Shows "Mod Enabled!" in green chat text when activated
  - Shows "Mod Disabled!" in red chat text when deactivated

## Installation

1. Install Minecraft 1.21.4 with Fabric Loader
2. Install **Fabric API**
3. (Optional) Install Mod Menu for in-game config
4. Place `autoanswer-1.0.0.jar` in your `.minecraft/mods` folder
5. Launch Minecraft!

## How to Use

### Math Equations
The mod automatically solves any math it sees in chat:
- Someone types: `5+3` → Mod answers: `8`
- Someone types: `(10+5)*2` → Mod answers: `30`

### Custom Prompts
1. **Open config file**: `config/autoanswer.json`
2. **Add your prompts**:
```json
{
  "red": "R3ass0n",
  "blue": "Skywars",
  "green": "MyAnswer"
}
```
3. **Save the file**
4. **Reload** (either restart Minecraft or use Mod Menu → Reload Config)

Now when someone types a message containing "red", the mod will automatically type "R3ass0n" in chat!

### With Mod Menu
1. Press **ESC** in-game
2. Click **Mods**
3. Find **Auto Answer Mod**
4. Click **Config**
5. Use buttons to open config folder or reload

## Configuration Example

`config/autoanswer.json`:
```json
{
  "prompt1": "answer1",
  "prompt2": "answer2"
}
```

- **Prompts** can be any text (case-insensitive)
- **Answers** are what the mod will type in chat
- The mod checks if chat messages **contain** your prompts

## Building from Source

```bash
./gradlew build
```
Output: `build/libs/autoanswer-1.0.0.jar`

## How It Works

1. Mixin intercepts incoming chat messages
2. Checks for math equations first
3. If not math, checks for your custom prompts
4. Automatically sends answer to chat
5. 1-second cooldown prevents spam

## License

MIT License
