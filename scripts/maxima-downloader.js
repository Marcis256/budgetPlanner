
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function downloadMaximaReceipts(email, password) {
  console.log("🚀 Startējam pārlūkprogrammu...");
  const browser = await chromium.launch({ headless: false, slowMo: 500 });
  const context = await browser.newContext({ acceptDownloads: true });
  const page = await context.newPage();

  console.log("🌐 Atveram maxima.lv/paldies lapu...");
  await page.goto('https://www.maxima.lv/paldies');

  // Apstrādā sīkfailu paziņojumu
  try {
    await page.waitForSelector('text=Atļaut visus sīkfailus', { timeout: 3000 });
    await page.click('text=Atļaut visus sīkfailus');
    console.log("✅ Sīkfaili pieņemti.");
  } catch (e) {
    console.log("ℹ️ Sīkfailu paziņojums netika atrasts, turpinām.");
  }

  console.log("🔐 Piesakāmies profilā...");
  await page.fill('#MALoginForm_loginInput', email);
  await page.fill('#MALoginForm_password', password);
  await page.click('#submitLoginPaldies');

  console.log("⏳ Gaida navigāciju pēc pieslēgšanās...");
  await page.waitForNavigation();

  console.log("📄 Dodamies uz čeku lapu...");
  await page.goto('https://www.maxima.lv/paldies/paldies-konts/pirkumu-vesture');
  await page.waitForTimeout(3000);

  await page.fill('#date-start', '01.01.2025');
  await page.fill('#date-end', '16.04.2025');
  await page.click('text=Filtrēt');

  console.log("🔍 Meklējam PDF pogas un info pa rindiņām...");
  const rows = await page.$$('#purchase_operations_table .tr');

  for (let i = 0; i < rows.length; i++) {
    const row = rows[i];
    const dateEl = await row.$('.purchase-col-2 .multi-row-data .multi-row-item:first-child');
    const timeEl = await row.$('.purchase-col-2 .multi-row-data .multi-row-item:last-child');
    const locationEl = await row.$('.purchase-col-2 .multi-row-data');
    const pdfBtn = await row.$('a.button-download.icon-pdf');

    if (dateEl && timeEl && locationEl && pdfBtn) {
      const date = (await dateEl.textContent()).trim().replaceAll('.', '-');
      const time = (await timeEl.textContent()).trim().replaceAll(':', '-');
      const locationRaw = (await locationEl.textContent()).trim();
      const location = locationRaw.split('\n')[1]?.trim().replaceAll(',', '').replaceAll(' ', '_') || 'Unknown';

      const fileName = `receipt-${date}_${time}_${location}.pdf`;
      const filePath = path.resolve(__dirname, 'receipts', fileName);

      console.log(`⬇️ Lejuplādējam: ${fileName}`);
      const [ download ] = await Promise.all([
        page.waitForEvent('download'),
        pdfBtn.click()
      ]);
      await download.saveAs(filePath);
      console.log(`✅ Saglabāts: ${filePath}`);
    }
  }

  console.log("🎉 Viss paveikts, aizveram pārlūku.");
  await browser.close();
}

downloadMaximaReceipts('user', 'pw');
