class BiuEditText {
  static mount(target, options = {}) {
    const root = typeof target === "string" ? document.querySelector(target) : target;
    if (!root) {
      throw new Error("BiuEditText mount target was not found.");
    }

    if (root.__biuEditText) return root.__biuEditText;

    const instance = new BiuEditText(root, options);
    root.__biuEditText = instance;
    return instance;
  }

  static autoMount() {
    document.querySelectorAll("[data-biu-input]").forEach((root) => {
      BiuEditText.mount(root, {
        autoFocus: root.hasAttribute("data-auto-focus"),
        placeholder: root.getAttribute("data-placeholder") || undefined,
      });
    });
  }

  constructor(root, options = {}) {
    this.options = {
      ariaLabel: "Biu input",
      autoFocus: false,
      placeholder: "Type here...",
      palette: ["#ffffff", "#7cf8ff", "#b9ff8b", "#ffd36a", "#ff7b9b"],
      arcHeight: [170, 260],
      maxFliers: 120,
      maxParticles: 180,
      ...options,
    };
    this.root = this.prepareRoot(root);
    this.editor = root.querySelector("[data-biu-editor]");
    this.canvas = root.querySelector("[data-biu-canvas]");
    this.ctx = this.canvas.getContext("2d");
    this.particles = [];
    this.fliers = [];
    this.isComposing = false;
    this.lastTime = performance.now();
    this.pixelRatio = Math.min(window.devicePixelRatio || 1, 2);
    this.palette = this.options.palette;

    this.resize = this.resize.bind(this);
    this.frame = this.frame.bind(this);
    this.onInput = this.onInput.bind(this);
    this.onKeydown = this.onKeydown.bind(this);
    this.onCompositionStart = this.onCompositionStart.bind(this);
    this.onCompositionEnd = this.onCompositionEnd.bind(this);
    this.onRootClick = this.onRootClick.bind(this);

    document.body.appendChild(this.canvas);
    this.resize();
    this.bindEvents();
    this.frameId = requestAnimationFrame(this.frame);
    if (this.options.autoFocus) this.editor.focus();
  }

  prepareRoot(root) {
    root.classList.add("biu-input");
    root.setAttribute("data-biu-input", "");

    let editor = root.querySelector("[data-biu-editor]");
    if (!editor) {
      editor = document.createElement("div");
      editor.className = "biu-editor";
      editor.setAttribute("data-biu-editor", "");
      root.appendChild(editor);
    }

    editor.classList.add("biu-editor");
    editor.setAttribute("data-biu-editor", "");
    editor.setAttribute("contenteditable", "true");
    editor.setAttribute("spellcheck", "false");
    editor.setAttribute("role", "textbox");
    editor.setAttribute("aria-multiline", "true");
    editor.setAttribute("aria-label", this.options.ariaLabel);
    editor.setAttribute("data-placeholder", this.options.placeholder);

    let canvas = root.querySelector("[data-biu-canvas]");
    if (!canvas) {
      canvas = document.createElement("canvas");
      canvas.className = "biu-effects";
      canvas.setAttribute("data-biu-canvas", "");
      root.appendChild(canvas);
    }

    canvas.classList.add("biu-effects");
    canvas.setAttribute("data-biu-canvas", "");

    return root;
  }

  bindEvents() {
    window.addEventListener("resize", this.resize);
    this.root.addEventListener("click", this.onRootClick);
    this.editor.addEventListener("beforeinput", this.onInput);
    this.editor.addEventListener("keydown", this.onKeydown);
    this.editor.addEventListener("compositionstart", this.onCompositionStart);
    this.editor.addEventListener("compositionend", this.onCompositionEnd);
  }

  destroy() {
    window.removeEventListener("resize", this.resize);
    this.root.removeEventListener("click", this.onRootClick);
    this.editor.removeEventListener("beforeinput", this.onInput);
    this.editor.removeEventListener("keydown", this.onKeydown);
    this.editor.removeEventListener("compositionstart", this.onCompositionStart);
    this.editor.removeEventListener("compositionend", this.onCompositionEnd);
    cancelAnimationFrame(this.frameId);
    this.canvas.remove();
    delete this.root.__biuEditText;
  }

  focus() {
    this.editor.focus();
  }

  getValue() {
    return this.editor.innerText;
  }

  setValue(value) {
    this.editor.textContent = value;
  }

  onRootClick() {
    this.editor.focus();
  }

  resize() {
    this.pixelRatio = Math.min(window.devicePixelRatio || 1, 2);
    this.canvas.width = Math.max(1, Math.floor(window.innerWidth * this.pixelRatio));
    this.canvas.height = Math.max(1, Math.floor(window.innerHeight * this.pixelRatio));
    this.canvas.style.width = `${window.innerWidth}px`;
    this.canvas.style.height = `${window.innerHeight}px`;
    this.ctx.setTransform(this.pixelRatio, 0, 0, this.pixelRatio, 0, 0);
  }

  onInput(event) {
    if (this.isComposing || event.isComposing || event.inputType === "insertCompositionText") {
      return;
    }

    if (event.inputType !== "insertText" || !event.data) return;
    event.preventDefault();

    for (const char of event.data) {
      if (char === "\n") continue;
      this.queueGlyph(char);
    }
  }

  onCompositionStart() {
    this.isComposing = true;
  }

  onCompositionEnd(event) {
    this.isComposing = false;
    const text = event.data || "";
    if (!text) return;

    window.setTimeout(() => {
      this.deleteBackwardCharacters([...text].length);
      for (const char of text) {
        this.queueGlyph(char);
      }
    }, 0);
  }

  onKeydown(event) {
    if (event.key !== "Enter") return;
    event.preventDefault();
    this.insertLineBreak();
  }

  queueGlyph(char) {
    const placeholder = this.insertPendingGlyph(char);
    requestAnimationFrame(() => this.launchGlyph(placeholder, char));
  }

  insertPendingGlyph(char) {
    const selection = window.getSelection();
    const range = selection && selection.rangeCount > 0
      ? selection.getRangeAt(0)
      : document.createRange();

    if (!selection || selection.rangeCount === 0) {
      range.selectNodeContents(this.editor);
      range.collapse(false);
    }

    range.deleteContents();

    const placeholder = document.createElement("span");
    placeholder.className = "biu-pending";
    placeholder.textContent = char;
    range.insertNode(placeholder);

    range.setStartAfter(placeholder);
    range.collapse(true);
    selection.removeAllRanges();
    selection.addRange(range);

    return placeholder;
  }

  insertLineBreak() {
    const selection = window.getSelection();
    if (!selection || selection.rangeCount === 0) return;

    const range = selection.getRangeAt(0);
    range.deleteContents();
    range.insertNode(document.createElement("br"));
    range.collapse(false);
    selection.removeAllRanges();
    selection.addRange(range);
  }

  deleteBackwardCharacters(count) {
    const selection = window.getSelection();
    if (!selection || selection.rangeCount === 0) return;

    if (typeof selection.modify === "function") {
      selection.collapseToEnd();
      for (let index = 0; index < count; index += 1) {
        selection.modify("extend", "backward", "character");
      }
      selection.deleteFromDocument();
      selection.collapseToEnd();
      return;
    }

    const range = selection.getRangeAt(0);
    let node = range.startContainer;
    let offset = range.startOffset;

    while (count > 0 && node) {
      if (node.nodeType === Node.TEXT_NODE && offset > 0) {
        const text = node.textContent || "";
        const chars = [...text.slice(0, offset)];
        const removeCount = Math.min(count, chars.length);
        const keepText = chars.slice(0, chars.length - removeCount).join("") + text.slice(offset);
        node.textContent = keepText;
        offset -= removeCount;
        count -= removeCount;
      }
      node = this.previousTextNode(node);
      offset = node?.textContent?.length || 0;
    }
  }

  previousTextNode(node) {
    let current = node;
    while (current && current !== this.editor) {
      if (current.previousSibling) {
        current = current.previousSibling;
        while (current.lastChild) current = current.lastChild;
        if (current.nodeType === Node.TEXT_NODE) return current;
      }
      current = current.parentNode;
    }
    return null;
  }

  getPointForElement(element) {
    const rect = element.getBoundingClientRect();

    return {
      x: rect.left + rect.width * 0.5,
      y: rect.top + rect.height * 0.58,
    };
  }

  launchGlyph(placeholder, char) {
    if (!placeholder.isConnected) return;

    const target = this.getPointForElement(placeholder);
    const start = {
      x: target.x + (Math.random() - 0.5) * Math.min(460, window.innerWidth * 0.7),
      y: window.innerHeight + 54 + Math.random() * 28,
    };
    const [minArcHeight, maxArcHeight] = this.options.arcHeight;
    const arcHeight = minArcHeight + Math.random() * (maxArcHeight - minArcHeight);
    const apexY = Math.max(28, target.y - arcHeight);
    const gravity = 2600 + Math.random() * 420;
    const initialVy = -Math.sqrt(Math.max(0, 2 * gravity * (start.y - apexY)));
    const fallSpeedAtTarget = Math.sqrt(Math.max(0, 2 * gravity * (target.y - apexY)));
    const duration = (-initialVy + fallSpeedAtTarget) / gravity;

    this.fliers.push({
      char,
      placeholder,
      targetX: target.x,
      targetY: target.y,
      apexY,
      x: start.x,
      y: start.y,
      vx: (target.x - start.x) / duration,
      vy: initialVy,
      gravity,
      age: 0,
      life: duration,
      rotation: (Math.random() - 0.5) * 0.55,
      spin: (Math.random() - 0.5) * 1.8,
      color: this.palette[Math.floor(Math.random() * this.palette.length)],
      size: parseFloat(getComputedStyle(this.editor).fontSize),
    });

    if (this.fliers.length > this.options.maxFliers) {
      this.fliers.splice(0, this.fliers.length - this.options.maxFliers);
    }
  }

  emitPulse(origin, color, scale = 1) {
    this.particles.push({
      type: "pulse",
      x: origin.x,
      y: origin.y,
      age: 0,
      life: 0.34,
      size: 26 * scale,
      color,
    });

    if (this.particles.length > this.options.maxParticles) {
      this.particles.splice(0, this.particles.length - this.options.maxParticles);
    }
  }

  frame(now) {
    const dt = Math.min(0.032, (now - this.lastTime) / 1000);
    this.lastTime = now;
    this.update(dt);
    this.draw();
    this.frameId = requestAnimationFrame(this.frame);
  }

  update(dt) {
    this.fliers = this.fliers.filter((flier) => {
      flier.age += dt;

      if (flier.age >= flier.life) {
        this.landGlyph(flier);
        return false;
      }

      flier.x += flier.vx * dt;
      flier.y += flier.vy * dt;
      flier.vy += flier.gravity * dt;
      flier.rotation += flier.spin * dt;
      return true;
    });

    this.particles = this.particles.filter((particle) => {
      particle.age += dt;
      if (particle.age >= particle.life) return false;

      if (particle.type !== "pulse") {
        particle.vy += particle.gravity * dt;
        particle.x += particle.vx * dt;
        particle.y += particle.vy * dt;
        particle.vx *= 0.986;
        particle.vy *= 0.986;
        particle.rotation += particle.spin * dt;
      }

      return true;
    });
  }

  landGlyph(flier) {
    if (!flier.placeholder.isConnected) return;

    flier.placeholder.classList.remove("biu-pending");
    flier.placeholder.classList.add("biu-landed");
    this.emitPulse({ x: flier.targetX, y: flier.targetY }, flier.color, 0.56);

    window.setTimeout(() => {
      if (!flier.placeholder.isConnected) return;
      flier.placeholder.classList.remove("biu-landed");
    }, 220);
  }

  draw() {
    this.ctx.clearRect(0, 0, window.innerWidth, window.innerHeight);

    for (const flier of this.fliers) {
      this.drawFlyingGlyph(flier);
    }

    for (const particle of this.particles) {
      const progress = particle.age / particle.life;
      const alpha = Math.sin((1 - progress) * Math.PI * 0.5);

      if (particle.type === "pulse") {
        this.drawPulse(particle, progress, alpha);
      } else if (particle.type === "glyph") {
        this.drawGlyph(particle, alpha);
      } else {
        this.drawStreak(particle, alpha);
      }
    }
  }

  drawFlyingGlyph(flier) {
    const progress = flier.age / flier.life;
    const squash = progress > 0.88 ? 1 - (progress - 0.88) * 1.4 : 1;
    const glow = 0.45 + Math.sin(progress * Math.PI) * 0.55;

    this.ctx.save();
    this.ctx.translate(flier.x, flier.y);
    this.ctx.rotate(flier.rotation);
    this.ctx.scale(1 + (1 - squash) * 0.18, squash);
    this.ctx.font = `800 ${flier.size}px "SFMono-Regular", Consolas, monospace`;
    this.ctx.textAlign = "center";
    this.ctx.textBaseline = "middle";
    this.ctx.shadowColor = flier.color;
    this.ctx.shadowBlur = 18 + glow * 20;
    this.ctx.fillStyle = this.hexToRgba(flier.color, 0.92);
    this.ctx.fillText(flier.char, 0, 0);

    this.ctx.globalAlpha = 0.22;
    this.ctx.fillStyle = "#ffffff";
    this.ctx.fillText(flier.char, -1, -1);
    this.ctx.restore();

    this.drawMotionTrail(flier, progress);
  }

  drawMotionTrail(flier, progress) {
    const tail = 22 + Math.sin(progress * Math.PI) * 30;
    const speedAngle = Math.atan2(flier.vy, flier.vx);
    const x2 = flier.x - Math.cos(speedAngle) * tail;
    const y2 = flier.y - Math.sin(speedAngle) * tail;

    const gradient = this.ctx.createLinearGradient(flier.x, flier.y, x2, y2);
    gradient.addColorStop(0, this.hexToRgba(flier.color, 0.42));
    gradient.addColorStop(1, this.hexToRgba(flier.color, 0));

    this.ctx.save();
    this.ctx.lineCap = "round";
    this.ctx.lineWidth = Math.max(2, flier.size * 0.12);
    this.ctx.shadowColor = flier.color;
    this.ctx.shadowBlur = 18;
    this.ctx.strokeStyle = gradient;
    this.ctx.beginPath();
    this.ctx.moveTo(flier.x, flier.y);
    this.ctx.lineTo(x2, y2);
    this.ctx.stroke();
    this.ctx.restore();
  }

  drawPulse(particle, progress, alpha) {
    const radius = particle.size * (0.45 + progress * 1.9);
    const gradient = this.ctx.createRadialGradient(
      particle.x,
      particle.y,
      0,
      particle.x,
      particle.y,
      radius,
    );
    gradient.addColorStop(0, this.hexToRgba(particle.color, alpha * 0.8));
    gradient.addColorStop(0.35, this.hexToRgba(particle.color, alpha * 0.28));
    gradient.addColorStop(1, this.hexToRgba(particle.color, 0));

    this.ctx.fillStyle = gradient;
    this.ctx.beginPath();
    this.ctx.arc(particle.x, particle.y, radius, 0, Math.PI * 2);
    this.ctx.fill();
  }

  drawGlyph(particle, alpha) {
    this.ctx.save();
    this.ctx.translate(particle.x, particle.y);
    this.ctx.rotate(particle.rotation);
    this.ctx.font = `700 ${particle.size}px "SFMono-Regular", Consolas, monospace`;
    this.ctx.textAlign = "center";
    this.ctx.textBaseline = "middle";
    this.ctx.shadowColor = particle.color;
    this.ctx.shadowBlur = 18;
    this.ctx.fillStyle = this.hexToRgba(particle.color, alpha);
    this.ctx.fillText(particle.text, 0, 0);
    this.ctx.restore();
  }

  drawStreak(particle, alpha) {
    const tail = 18 + particle.size * 0.9;
    const angle = Math.atan2(particle.vy, particle.vx);
    const x2 = particle.x - Math.cos(angle) * tail;
    const y2 = particle.y - Math.sin(angle) * tail;

    const gradient = this.ctx.createLinearGradient(particle.x, particle.y, x2, y2);
    gradient.addColorStop(0, this.hexToRgba(particle.color, alpha));
    gradient.addColorStop(1, this.hexToRgba(particle.color, 0));

    this.ctx.save();
    this.ctx.lineCap = "round";
    this.ctx.lineWidth = Math.max(1.2, particle.size * 0.18);
    this.ctx.shadowColor = particle.color;
    this.ctx.shadowBlur = 14;
    this.ctx.strokeStyle = gradient;
    this.ctx.beginPath();
    this.ctx.moveTo(particle.x, particle.y);
    this.ctx.lineTo(x2, y2);
    this.ctx.stroke();
    this.ctx.restore();
  }

  hexToRgba(hex, alpha) {
    const value = hex.replace("#", "");
    const red = parseInt(value.slice(0, 2), 16);
    const green = parseInt(value.slice(2, 4), 16);
    const blue = parseInt(value.slice(4, 6), 16);
    return `rgba(${red}, ${green}, ${blue}, ${alpha})`;
  }
}

window.BiuEditText = BiuEditText;

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => BiuEditText.autoMount());
} else {
  BiuEditText.autoMount();
}
