# 1. Bāzes attēls
FROM node:18-alpine

# 2. Darba direktorija konteinerī
WORKDIR /app

# 3. Nokopē package.json un package-lock.json
COPY package*.json ./

# 4. Instalē atkarības
RUN npm install

# 5. Nokopē pārējos failus
COPY . .

# 6. Uzbūvē frontend aplikāciju
RUN npm run build

# 7. Serveris, kas kalpos build rezultātu (vēlāk būs nginx vai http-server)
RUN npm install -g serve
CMD ["serve", "-s", "build", "-l", "3000"]

# 8. Ports
EXPOSE 3000
