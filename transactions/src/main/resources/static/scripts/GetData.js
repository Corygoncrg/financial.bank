document.addEventListener('DOMContentLoaded', () => {
    fetch('/transactions')
        .then(response => response.json())
        .then(transactions => {
            const list = document.querySelector('.list__div ul');
            list.innerHTML = '';


            transactions.forEach(transaction => {
                const listItem = document.createElement('li');
                listItem.classList.add('transaction__list');

                const transactionDate = document.createElement('p');
                transactionDate.textContent = `Transaction Date: ${new Date(transaction.transactionDate).toLocaleString()}`;
                
                const importDate = document.createElement('p');
                importDate.textContent = `Import Date: ${new Date(transaction.importDate).toLocaleString()}`;

                listItem.appendChild(transactionDate);
                listItem.appendChild(importDate);
                list.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
});