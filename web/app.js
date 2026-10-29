"use strict";

// 当前选中的运算符，默认乘法（与 Lab 6 的 demo 6 * 7 保持一致）
let currentOp = "*";

const aInput = document.getElementById("a");
const bInput = document.getElementById("b");
const resultEl = document.getElementById("result");
const exprEl = document.getElementById("expr");
const statusEl = document.getElementById("status");

// 运算符按钮：点击后切换 currentOp，并高亮当前按钮
document.querySelectorAll(".op").forEach((btn) => {
  btn.addEventListener("click", () => {
    currentOp = btn.dataset.op;
    document.querySelectorAll(".op").forEach((other) => {
      other.classList.toggle("active", other === btn);
    });
  });
});

// 启动时探测后端健康接口，在页面上显示后端是否可用
async function checkBackend() {
  try {
    const res = await fetch("/api/health", { cache: "no-store" });
    if (!res.ok) throw new Error("HTTP " + res.status);
    statusEl.textContent = "后端服务：在线";
    statusEl.className = "status status--ok";
  } catch (err) {
    statusEl.textContent = "后端服务：离线";
    statusEl.className = "status status--fail";
  }
}

// 调用后端 /api/calc：前端只写相对路径，由 nginx 代理到 backend:8000
async function calculate() {
  const a = aInput.value.trim();
  const b = bInput.value.trim();

  if (a === "" || b === "") {
    showError("请输入两个操作数");
    return;
  }

  const url = `/api/calc?a=${encodeURIComponent(a)}&op=${encodeURIComponent(currentOp)}&b=${encodeURIComponent(b)}`;
  try {
    const res = await fetch(url);
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "计算失败");

    resultEl.textContent = data.result;
    resultEl.className = "result";
    exprEl.textContent = `${a} ${currentOp} ${b} =`;
  } catch (err) {
    showError(err.message);
  }
}

function showError(message) {
  resultEl.textContent = message;
  resultEl.className = "result result--error";
  exprEl.textContent = "\u00a0";
}

document.getElementById("equals").addEventListener("click", calculate);

// 在任意输入框里按回车也触发计算，方便演示
[aInput, bInput].forEach((input) => {
  input.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      calculate();
    }
  });
});

checkBackend();
