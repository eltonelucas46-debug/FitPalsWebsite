(function () {
  "use strict";

  let deferredPrompt = null;

  // Installation banner
  const installBanner = document.createElement("div");
  installBanner.id = "fitpals-install-banner";

  Object.assign(installBanner.style, {
    position: "fixed",
    top: "12px",
    left: "12px",
    right: "12px",
    zIndex: "999999",
    display: "none",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "12px",
    padding: "12px 14px",
    borderRadius: "14px",
    background: "#2196f3",
    color: "#ffffff",
    boxShadow: "0 8px 25px rgba(0,0,0,.30)",
    fontFamily: "Arial, sans-serif"
  });

  // Text area
  const message = document.createElement("div");

  Object.assign(message.style, {
    flex: "1",
    minWidth: "0"
  });

  const title = document.createElement("div");
  title.textContent = "📱 Install FitPals";

  Object.assign(title.style, {
    fontSize: "15px",
    fontWeight: "700",
    marginBottom: "3px"
  });

  const subtitle = document.createElement("div");
  subtitle.textContent = "Get the FitPals app on your device.";

  Object.assign(subtitle.style, {
    fontSize: "12px",
    opacity: "0.9"
  });

  message.appendChild(title);
  message.appendChild(subtitle);

  // Install button
  const installButton = document.createElement("button");

  installButton.type = "button";
  installButton.textContent = "Install";

  Object.assign(installButton.style, {
    flexShrink: "0",
    padding: "9px 14px",
    border: "none",
    borderRadius: "10px",
    background: "#ffffff",
    color: "#2196f3",
    fontSize: "14px",
    fontWeight: "700",
    cursor: "pointer"
  });

  // Cancel button
  const cancelButton = document.createElement("button");

  cancelButton.type = "button";
  cancelButton.textContent = "✕";
  cancelButton.setAttribute("aria-label", "Cancel FitPals installation prompt");

  Object.assign(cancelButton.style, {
    flexShrink: "0",
    width: "32px",
    height: "32px",
    padding: "0",
    border: "none",
    borderRadius: "50%",
    background: "rgba(255,255,255,.18)",
    color: "#ffffff",
    fontSize: "18px",
    fontWeight: "700",
    cursor: "pointer"
  });

  installBanner.appendChild(message);
  installBanner.appendChild(installButton);
  installBanner.appendChild(cancelButton);

  // Add banner to page
  document.addEventListener("DOMContentLoaded", function () {
    document.body.appendChild(installBanner);
  });

  // Show banner
  function showInstallBanner() {
    installBanner.style.display = "flex";
  }

  // Hide banner
  function hideInstallBanner() {
    installBanner.style.display = "none";
  }

  // Browser's native PWA installation event
  window.addEventListener("beforeinstallprompt", function (event) {
    event.preventDefault();

    deferredPrompt = event;

    showInstallBanner();
  });

  // Install button
  installButton.addEventListener("click", async function () {

    // Native PWA installation
    if (deferredPrompt) {
      deferredPrompt.prompt();

      try {
        await deferredPrompt.userChoice;
      } catch (error) {
        console.log("FitPals installation prompt closed.");
      }

      deferredPrompt = null;
      hideInstallBanner();

      return;
    }

    // APK fallback
    const downloadAPK = confirm(
      "Install FitPals on Android?\n\n" +
      "Your browser will download the FitPals Android app (APK). " +
      "Android will then ask you to confirm the installation."
    );

    if (downloadAPK) {
      window.location.href = "/FitPalsWebsite/FitPals-Animated.apk";
    }
  });

  // Cancel button
  cancelButton.addEventListener("click", function () {
    hideInstallBanner();

    // Remember that the user cancelled it during this visit.
    sessionStorage.setItem("fitpalsInstallDismissed", "true");
  });

  // Hide after successful PWA installation
  window.addEventListener("appinstalled", function () {
    hideInstallBanner();
    deferredPrompt = null;

    console.log("FitPals was installed.");
  });

  // Don't show the banner again after the user cancelled it
  if (sessionStorage.getItem("fitpalsInstallDismissed") === "true") {
    hideInstallBanner();
  }

})();
