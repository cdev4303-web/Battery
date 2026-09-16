import fs from 'fs';
import path from 'path';

const srcDir = path.resolve('dist');
const destDir = path.resolve('app/src/main/assets/www');

function copyRecursive(src, dest) {
  if (!fs.existsSync(src)) {
    console.error(`Source directory ${src} does not exist! Run npm run build first.`);
    process.exit(1);
  }

  if (!fs.existsSync(dest)) {
    fs.mkdirSync(dest, { recursive: true });
  }

  const entries = fs.readdirSync(src, { withFileTypes: true });

  for (const entry of entries) {
    const srcPath = path.join(src, entry.name);
    const destPath = path.join(dest, entry.name);

    if (entry.isDirectory()) {
      copyRecursive(srcPath, destPath);
    } else {
      fs.copyFileSync(srcPath, destPath);
    }
  }
}

console.log(`Copying built web assets from ${srcDir} to ${destDir}...`);
copyRecursive(srcDir, destDir);
console.log('Web assets successfully copied into Android project!');
