const SITE = {
  email: "contato@confiapix.com.br",
  whatsapp: "",
};

const nav = document.querySelector("[data-nav]");
const menuToggle = document.querySelector("[data-menu-toggle]");
const year = document.querySelector("[data-year]");
const form = document.querySelector("[data-contact-form]");
const statusEl = document.querySelector("[data-form-status]");
const emailLink = document.querySelector("[data-contact-email]");
const whatsappRow = document.querySelector("[data-whatsapp-row]");
const whatsappLink = document.querySelector("[data-whatsapp-link]");

if (year) {
  year.textContent = String(new Date().getFullYear());
}

if (emailLink) {
  emailLink.href = `mailto:${SITE.email}`;
  emailLink.textContent = SITE.email;
}

if (SITE.whatsapp && whatsappRow && whatsappLink) {
  const phone = SITE.whatsapp.replace(/\D/g, "");
  whatsappLink.href = `https://wa.me/${phone}`;
  whatsappRow.hidden = false;
}

window.addEventListener("scroll", () => {
  nav?.classList.toggle("is-scrolled", window.scrollY > 12);
});

menuToggle?.addEventListener("click", () => {
  const open = nav?.classList.toggle("is-open");
  menuToggle.setAttribute("aria-label", open ? "Fechar menu" : "Abrir menu");
  menuToggle.querySelector("span").textContent = open ? "close" : "menu";
});

document.querySelector("[data-nav-links]")?.addEventListener("click", (event) => {
  if (event.target instanceof HTMLAnchorElement) {
    nav?.classList.remove("is-open");
  }
});

function setStatus(message, isError = false) {
  if (!statusEl) return;
  statusEl.textContent = message;
  statusEl.classList.toggle("is-error", isError);
}

form?.addEventListener("submit", (event) => {
  event.preventDefault();
  const data = new FormData(form);
  const name = String(data.get("name") || "").trim();
  const company = String(data.get("company") || "").trim();
  const email = String(data.get("email") || "").trim();
  const phone = String(data.get("phone") || "").trim();
  const message = String(data.get("message") || "").trim();

  if (!name || !company || !email || !message) {
    setStatus("Preencha nome, empresa, e-mail e a mensagem.", true);
    return;
  }

  const body = [
    `Nome: ${name}`,
    `Empresa: ${company}`,
    `E-mail: ${email}`,
    `WhatsApp: ${phone || "não informado"}`,
    "",
    message,
  ].join("\n");

  const mailto = `mailto:${SITE.email}?subject=${encodeURIComponent(
    `Demonstração ConfiaPix — ${company}`
  )}&body=${encodeURIComponent(body)}`;

  setStatus("Abrindo seu e-mail para enviar o pedido de demonstração...");
  window.location.href = mailto;
  form.reset();
});
